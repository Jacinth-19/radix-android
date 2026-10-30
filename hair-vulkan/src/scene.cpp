#include "scene.hpp"

#include <cmath>

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

void Mesh::computeNormals() {
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
    Vec3 triCentre = (a.pos + b.pos + c.pos) / 3.0f;
    if (dot(fn, triCentre - centre) < 0.0f) fn = -fn;
    a.normal += fn;
    b.normal += fn;
    c.normal += fn;
  }
  for (MeshVertex& v : vertices) v.normal = normalize(v.normal);
}

BodyScene buildBodyScene() {
  BodyScene scene;
  const Vec3 headCenter(0.0f, 1.60f, 0.0f);
  const Vec3 headRadii(0.081f, 0.110f, 0.095f);
  buildHead(scene.mesh, headCenter, headRadii);

  // Neck: tapers from the trapezius into the skull.
  const std::vector<ProfilePoint> neck = {
      {1.300f, 0.066f, 0.062f, 0.004f},
      {1.360f, 0.057f, 0.054f, 0.004f},
      {1.420f, 0.050f, 0.049f, 0.006f},
      {1.470f, 0.048f, 0.048f, 0.008f},
      {1.520f, 0.050f, 0.052f, 0.010f},
  };
  buildLathe(scene.mesh, neck, 64, false, false);

  // Shoulders + chest (a bust that is cropped below the frame).
  const std::vector<ProfilePoint> torso = {
      {1.520f, 0.056f, 0.052f, 0.008f},
      {1.485f, 0.070f, 0.058f, 0.007f},
      {1.450f, 0.092f, 0.064f, 0.005f},
      {1.415f, 0.126f, 0.072f, 0.003f},
      {1.380f, 0.170f, 0.084f, 0.001f},
      {1.352f, 0.203f, 0.093f, 0.000f},
      {1.325f, 0.223f, 0.100f, 0.000f},
      {1.295f, 0.228f, 0.105f, 0.000f},
      {1.255f, 0.219f, 0.109f, 0.000f},
      {1.200f, 0.208f, 0.112f, 0.000f},
      {1.140f, 0.199f, 0.110f, 0.000f},
      {1.070f, 0.193f, 0.106f, 0.000f},
      {0.980f, 0.188f, 0.102f, 0.000f},
      {0.860f, 0.183f, 0.099f, 0.000f},
      {0.700f, 0.178f, 0.096f, 0.000f},
      {0.520f, 0.174f, 0.094f, 0.000f},
      {0.300f, 0.172f, 0.093f, 0.000f},
      {0.040f, 0.176f, 0.096f, 0.000f},
      {0.000f, 0.185f, 0.104f, 0.000f},
  };
  buildLathe(scene.mesh, torso, 96, true, true);

  scene.mesh.computeNormals();

  // Collision proxy.
  scene.shape.headCenter = headCenter;
  scene.shape.headRadii = headRadii;
  scene.shape.groundY = 0.0f;
  scene.shape.solids = {
      {headCenter, Vec3(headRadii.x * 0.99f, headRadii.y * 0.99f, headRadii.z * 0.99f)},
      {Vec3(0.0f, 1.435f, 0.006f), Vec3(0.052f, 0.075f, 0.052f)},   // neck
      {Vec3(0.0f, 1.330f, 0.0f), Vec3(0.222f, 0.060f, 0.100f)},     // shoulders
      {Vec3(0.0f, 1.235f, 0.0f), Vec3(0.200f, 0.085f, 0.104f)},     // chest
      {Vec3(0.0f, 1.100f, 0.0f), Vec3(0.190f, 0.120f, 0.102f)},     // lower chest
      // Face guard: keeps combed hair off the face (nose/brow/cheek volume).
      {Vec3(0.0f, 1.596f, 0.050f), Vec3(0.062f, 0.074f, 0.042f)},
  };
  return scene;
}

}  // namespace hv
