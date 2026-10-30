// hair-vulkan: procedural bust (head / neck / shoulders) + orbit camera.
#pragma once

#include <string>
#include <vector>

#include "hair.hpp"
#include "math.hpp"

namespace hv {

struct MeshVertex {
  Vec3 pos;
  Vec3 normal;
  Vec2 uv;
};

struct Mesh {
  std::vector<MeshVertex> vertices;
  std::vector<uint32_t> indices;
  void computeNormals(bool orientOutward = true);
};

// Builds the bust (scanned head + neck + shoulders, continued into a torso) and
// the matching collision proxy used by the hair simulation.
struct BodyScene {
  Mesh mesh;    // skin: scanned bust + torso continuation
  Mesh eyes;    // the two eyeballs
  BodyShape shape;
  Vec3 eyeCenters[2];
  float eyeRadius = 0.012f;
  bool loadedHeadAsset = false;
};

// `assetsDir` holds head.obj, eyes.obj and head_sdf.bin (see tools/build_head_asset.py).
BodyScene buildBodyScene(const std::string& assetsDir);

// Minimal OBJ reader (v + f), welded and re-normalled.
bool loadObjMesh(const std::string& path, Mesh& mesh, std::string* error);

struct Camera {
  Vec3 target{0.0f, 1.56f, 0.0f};
  float yaw = 0.55f;    // radians, 0 = looking from +z
  float pitch = 0.06f;  // radians, positive = above
  float dist = 0.95f;
  float fovDeg = 34.0f;

  Vec3 eye() const {
    Vec3 dir(std::sin(yaw) * std::cos(pitch), std::sin(pitch), std::cos(yaw) * std::cos(pitch));
    return target + dir * dist;
  }
  Mat4 view() const {
    return Mat4::lookAt(eye(), target, Vec3(0, 1, 0));
  }
  Mat4 proj(float aspect) const {
    return Mat4::perspective(fovDeg * DEG, aspect, 0.02f, 120.0f);
  }
  // Camera controls (orbit / dolly / pan), used by the scripted fly-through and
  // available for any interactive front-end.
  void orbit(float dYaw, float dPitch) {
    yaw += dYaw;
    pitch = clampf(pitch + dPitch, -1.35f, 1.35f);
  }
  void dolly(float amount) { dist = clampf(dist * std::exp(-amount), 0.12f, 12.0f); }
  void pan(float dx, float dy) {
    Vec3 f = normalize(target - eye());
    Vec3 r = normalize(cross(f, Vec3(0, 1, 0)));
    Vec3 u = cross(r, f);
    target += r * dx + u * dy;
  }
};

}  // namespace hv
