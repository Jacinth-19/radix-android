#include "scene.hpp"

#include <algorithm>
#include <cmath>
#include <cstdio>
#include <cstring>
#include <cstdlib>
#include <map>
#include <string>

namespace hv {
namespace {

inline void addQuad(std::vector<uint32_t>& idx, uint32_t a, uint32_t b, uint32_t c, uint32_t d) {
  idx.push_back(a); idx.push_back(b); idx.push_back(c);
  idx.push_back(a); idx.push_back(c); idx.push_back(d);
}

// ---------------------------------------------------------------- head ------
// Unit-sphere direction (+y up, +z facing the viewer) -> local head coordinates.
Vec3 headLocal(const Vec3& n) {
  const float x = n.x, y = n.y, z = n.z;
  Vec3 l = n;

  // Skull: slightly narrower at the back, fuller at the crown, wider temples.
  const float backness = saturate(-z);
  l.z *= 1.0f - 0.05f * backness * backness;
  l.x *= 1.0f + 0.06f * smoothstepf(0.20f, 0.75f, y) * (1.0f - smoothstepf(0.80f, 1.0f, y));

  // Jaw: taper the lower half towards the chin.
  const float lower = saturate(-y);
  const float taper = 1.0f - 0.40f * std::pow(lower, 1.45f);
  l.x *= taper;
  l.z *= 1.0f - 0.16f * std::pow(lower, 1.25f);

  // Chin: forward rounding bump.
  const float chin = std::exp(-(x * x) / 0.055f) * std::exp(-std::pow((y + 0.78f) / 0.20f, 2.0f)) *
                     saturate(z * 2.0f);
  l.z += chin * 0.18f;
  l.y -= chin * 0.045f;

  // Brow ridge.
  const float brow = std::exp(-(x * x) / 0.22f) * std::exp(-std::pow((y - 0.20f) / 0.10f, 2.0f)) *
                     saturate(z * 1.5f);
  l.z += brow * 0.085f;

  // Eye sockets (slight depression above the cheeks).
  const float eye = std::exp(-std::pow((std::fabs(x) - 0.31f) / 0.17f, 2.0f)) *
                    std::exp(-std::pow((y - 0.06f) / 0.15f, 2.0f)) * saturate(z * 1.5f);
  l.z -= eye * 0.085f;
  // Eyelids/brows: a shallow lid ridge above the socket.
  const float lid = std::exp(-std::pow((std::fabs(x) - 0.30f) / 0.16f, 2.0f)) *
                    std::exp(-std::pow((y + 0.02f) / 0.05f, 2.0f)) * saturate(z * 1.5f);
  l.z += lid * 0.045f;

  // Nose: ridge + tip with nostrils flaring a little.
  const float noseMask = std::exp(-(x * x) / 0.010f) * std::exp(-std::pow((y + 0.05f) / 0.30f, 2.0f)) *
                         saturate(z * 1.2f);
  const float noseTip = std::exp(-(x * x) / 0.020f) * std::exp(-std::pow((y + 0.26f) / 0.10f, 2.0f)) *
                        saturate(z * 1.2f);
  l.z += noseMask * 0.11f + noseTip * 0.15f;
  // Nose bridge narrows towards the tip.
  l.x *= 1.0f - 0.35f * noseTip;

  // Cheekbones.
  const float cheek = std::exp(-std::pow((std::fabs(x) - 0.42f) / 0.22f, 2.0f)) *
                      std::exp(-std::pow((y + 0.30f) / 0.22f, 2.0f)) * saturate(z * 1.2f);
  l.z += cheek * 0.055f;
  l.x += std::exp(-std::pow((y + 0.22f) / 0.30f, 2.0f)) * saturate(z) * 0.02f * (x > 0 ? 1.0f : -1.0f);

  // Lips.
  const float lips = std::exp(-(x * x) / 0.012f) * std::exp(-std::pow((y + 0.44f) / 0.055f, 2.0f)) *
                     saturate(z * 1.2f);
  l.z += lips * 0.075f;

  // Flatten the top of the skull a touch.
  l.y *= 1.0f - 0.035f * smoothstepf(0.55f, 1.0f, y);

  return l;
}

void buildHead(Mesh& mesh, const Vec3& center, const Vec3& radii) {
  const int latSeg = 110;  // pole to pole
  const int lonSeg = 128;  // around
  const uint32_t base = (uint32_t)mesh.vertices.size();

  for (int i = 0; i <= latSeg; ++i) {
    const float v = (float)i / (float)latSeg;
    const float phi = v * PI;
    for (int j = 0; j <= lonSeg; ++j) {
      const float u = (float)j / (float)lonSeg;
      const float theta = u * 2.0f * PI;
      Vec3 n(std::sin(phi) * std::sin(theta), std::cos(phi), std::sin(phi) * std::cos(theta));
      Vec3 local = headLocal(n);
      MeshVertex mv;
      mv.pos = center + Vec3(local.x * radii.x, local.y * radii.y, local.z * radii.z);
      mv.normal = normalize(Vec3(local.x, local.y, local.z));
      mv.uv = Vec2(u, v);
      mesh.vertices.push_back(mv);
    }
  }
  const uint32_t stride = (uint32_t)(lonSeg + 1);
  for (int i = 0; i < latSeg; ++i) {
    for (int j = 0; j < lonSeg; ++j) {
      const uint32_t a = base + (uint32_t)i * stride + (uint32_t)j;
      const uint32_t b = a + 1;
      const uint32_t c = a + stride + 1;
      const uint32_t d = a + stride;
      addQuad(mesh.indices, a, b, c, d);
    }
  }
}

// Lathe of elliptical cross-sections: (y, radiusX, radiusZ, zOffset)
struct ProfilePoint {
  float y, rx, rz, zoff;
};

void buildLathe(Mesh& mesh, const std::vector<ProfilePoint>& profile, int segments, bool capTop,
                bool capBottom) {
  const uint32_t base = (uint32_t)mesh.vertices.size();
  for (size_t k = 0; k < profile.size(); ++k) {
    const ProfilePoint& p = profile[k];
    for (int j = 0; j <= segments; ++j) {
      const float u = (float)j / (float)segments;
      const float theta = u * 2.0f * PI;
      MeshVertex mv;
      const float sn = std::sin(theta), cs = std::cos(theta);
      mv.pos = Vec3(sn * p.rx, p.y, cs * p.rz + p.zoff);
      mv.normal = normalize(Vec3(sn / std::max(1e-4f, p.rx), 0.0f, cs / std::max(1e-4f, p.rz)));
      mv.uv = Vec2(u, (float)k / (float)(profile.size() - 1));
      mesh.vertices.push_back(mv);
    }
  }
  const uint32_t stride = (uint32_t)segments + 1;
  for (size_t k = 0; k + 1 < profile.size(); ++k) {
    for (int j = 0; j < segments; ++j) {
      const uint32_t a = base + (uint32_t)k * stride + (uint32_t)j;
      addQuad(mesh.indices, a, a + 1, a + stride + 1, a + stride);
    }
  }
  if (capTop) {
    const uint32_t c = (uint32_t)mesh.vertices.size();
    const ProfilePoint& p = profile.front();
    MeshVertex mv;
    mv.pos = Vec3(0, p.y, p.zoff);
    mv.normal = Vec3(0, 1, 0);
    mv.uv = Vec2(0.5f, 1.0f);
    mesh.vertices.push_back(mv);
    for (int j = 0; j < segments; ++j) {
      const uint32_t a = base + (uint32_t)j;
      mesh.indices.push_back(c);
      mesh.indices.push_back(a + 1);
      mesh.indices.push_back(a);
    }
  }
  if (capBottom) {
    const uint32_t c = (uint32_t)mesh.vertices.size();
    const ProfilePoint& p = profile.back();
    MeshVertex mv;
    mv.pos = Vec3(0, p.y, p.zoff);
    mv.normal = Vec3(0, -1, 0);
    mv.uv = Vec2(0.5f, 0.0f);
    mesh.vertices.push_back(mv);
    const uint32_t ring = base + (uint32_t)(profile.size() - 1) * stride;
    for (int j = 0; j < segments; ++j) {
      const uint32_t a = ring + (uint32_t)j;
      mesh.indices.push_back(c);
      mesh.indices.push_back(a);
      mesh.indices.push_back(a + 1);
    }
  }
}

}  // namespace

void Mesh::computeNormals(bool orientOutward) {
  for (MeshVertex& v : vertices) v.normal = Vec3(0, 0, 0);
  // Centre used to orient every face normal outwards (the bust is star-shaped).
  Vec3 centre(0, 0, 0);
  for (const MeshVertex& v : vertices) centre += v.pos;
  if (!vertices.empty()) centre = centre / (float)vertices.size();
  for (size_t i = 0; i + 2 < indices.size(); i += 3) {
    MeshVertex& a = vertices[indices[i]];
    MeshVertex& b = vertices[indices[i + 1]];
    MeshVertex& c = vertices[indices[i + 2]];
    Vec3 fn = cross(b.pos - a.pos, c.pos - a.pos);
    if (orientOutward) {
      Vec3 triCentre = (a.pos + b.pos + c.pos) / 3.0f;
      if (dot(fn, triCentre - centre) < 0.0f) fn = -fn;
    }
    a.normal += fn;
    b.normal += fn;
    c.normal += fn;
  }
  for (MeshVertex& v : vertices) v.normal = normalize(v.normal);
}

bool loadObjMesh(const std::string& path, Mesh& mesh, std::string* error) {
  std::FILE* f = std::fopen(path.c_str(), "rb");
  if (!f) {
    if (error) *error = "cannot open " + path;
    return false;
  }
  std::vector<Vec3> positions;
  std::vector<uint32_t> faces;
  char line[512];
  while (std::fgets(line, sizeof(line), f)) {
    if (line[0] == 'v' && (line[1] == ' ' || line[1] == '\t')) {
      float x = 0, y = 0, z = 0;
      if (std::sscanf(line + 2, "%f %f %f", &x, &y, &z) == 3) positions.push_back(Vec3(x, y, z));
    } else if (line[0] == 'f' && (line[1] == ' ' || line[1] == '\t')) {
      // Faces may be "a b c", "a/b/c" or "a//c": only the vertex index matters.
      long v[8] = {0};
      int n = 0;
      const char* q = line + 2;
      while (*q && n < 8) {
        while (*q == ' ' || *q == '\t') ++q;
        if (!*q) break;
        char* endp = nullptr;
        const long idx = std::strtol(q, &endp, 10);
        if (endp == q) break;
        v[n++] = idx - 1;
        q = endp;
        while (*q && *q != ' ' && *q != '\t') ++q;  // skip /uv/normal
      }
      for (int k = 2; k < n; ++k) {  // triangulate the polygon
        faces.push_back((uint32_t)v[0]);
        faces.push_back((uint32_t)v[k - 1]);
        faces.push_back((uint32_t)v[k]);
      }
    }
  }
  std::fclose(f);
  if (positions.empty() || faces.empty()) {
    if (error) *error = "no geometry in " + path;
    return false;
  }
  mesh.vertices.clear();
  mesh.indices = faces;
  mesh.vertices.resize(positions.size());
  for (size_t i = 0; i < positions.size(); ++i) {
    mesh.vertices[i].pos = positions[i];
    mesh.vertices[i].normal = Vec3(0, 1, 0);
    mesh.vertices[i].uv = Vec2(0.0f, 0.0f);
  }
  mesh.computeNormals(true);   // orient by centroid: the scan has a few flipped triangles
  return true;
}

namespace {

// Torso continuation: keeps the cropped scan from ending in mid air so the long
// hair has a chest and a waist to fall over. Top ring matches the scan's chest.
void buildTorso(Mesh& mesh) {
  const std::vector<ProfilePoint> profile = {
      {1.400f, 0.100f, 0.055f, -0.055f},
      {1.352f, 0.168f, 0.108f, -0.048f},
      {1.320f, 0.184f, 0.118f, -0.044f},
      {1.285f, 0.178f, 0.119f, -0.038f},
      {1.280f, 0.172f, 0.120f, -0.038f},
      {1.230f, 0.161f, 0.116f, -0.030f},
      {1.170f, 0.148f, 0.110f, -0.020f},
      {1.110f, 0.134f, 0.103f, -0.012f},
      {1.050f, 0.121f, 0.096f, -0.006f},
      {0.990f, 0.110f, 0.090f, -0.002f},
      {0.920f, 0.101f, 0.085f, 0.000f},
      {0.820f, 0.096f, 0.081f, 0.000f},
      {0.640f, 0.092f, 0.078f, 0.000f},
      {0.320f, 0.090f, 0.076f, 0.000f},
      {0.040f, 0.094f, 0.080f, 0.000f},
      {0.000f, 0.100f, 0.084f, 0.000f},
  };
  buildLathe(mesh, profile, 128, false, true);
}

// Screens the torso into the same mesh as the bust by appending it.
void appendMesh(Mesh& dst, const Mesh& src) {
  const uint32_t base = (uint32_t)dst.vertices.size();
  dst.vertices.insert(dst.vertices.end(), src.vertices.begin(), src.vertices.end());
  for (uint32_t i : src.indices) dst.indices.push_back(base + i);
}

}  // namespace

BodyScene buildBodyScene(const std::string& assetsDir) {
  BodyScene scene;
  Mesh head;
  std::string err;
  scene.loadedHeadAsset = loadObjMesh(assetsDir + "/head.obj", head, &err);
  if (!scene.loadedHeadAsset) {
    std::fprintf(stderr, "warning: %s (run tools/build_head_asset.py)\n", err.c_str());
    const Vec3 headCenter(0.0f, 1.545f, -0.005f);
    const Vec3 headRadii(0.079f, 0.112f, 0.092f);
    buildHead(scene.mesh, headCenter, headRadii);
  } else {
    scene.mesh = head;
  }

  Mesh eyes;
  if (loadObjMesh(assetsDir + "/eyes.obj", eyes, &err)) {
    scene.eyes = eyes;
  } else {
    std::fprintf(stderr, "warning: %s\n", err.c_str());
  }

  buildTorso(scene.mesh);
  scene.mesh.computeNormals();

  // Eyeball placement (matches tools/build_head_asset.py).
  const float eyeX = 0.0315f, eyeY = 1.5558f, eyeZ = 0.0343f;
  scene.eyeCenters[0] = Vec3(-eyeX, eyeY, eyeZ);
  scene.eyeCenters[1] = Vec3(eyeX, eyeY, eyeZ);
  scene.eyeRadius = 0.01172f;

  // Collision: exact SDF against the scanned bust + soft volumes for the torso.
  scene.shape.groundY = 0.0f;
  scene.shape.sdf.load(assetsDir + "/head_sdf.bin");
  scene.shape.headCenter = Vec3(0.0f, 1.545f, -0.005f);
  scene.shape.headRadii = Vec3(0.079f, 0.112f, 0.092f);
  scene.shape.solids = {
      {Vec3(0.0f, 1.240f, -0.030f), Vec3(0.172f, 0.120f, 0.118f)},  // chest
      {Vec3(0.0f, 1.080f, -0.012f), Vec3(0.130f, 0.110f, 0.100f)},  // ribs
      {Vec3(0.0f, 0.900f, 0.000f), Vec3(0.102f, 0.140f, 0.085f)},   // waist
      {Vec3(0.0f, 0.600f, 0.000f), Vec3(0.094f, 0.200f, 0.080f)},   // lower torso
  };
  return scene;
}

}  // namespace hv
