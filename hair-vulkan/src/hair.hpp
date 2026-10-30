// hair-vulkan: hair-mesh generation + real-time simulation.
//
// The hair is stored as a *hair mesh* (a regular (rows x cols) sheet of strands),
// following "Real-time Hair Mesh Simulation" (Wu & Yuksel, I3D 2016):
//
//   * every strand is a chain of particles with distance + bending constraints,
//   * the sheet is braced by horizontal and vertical link constraints, which act
//     as the volumetric (inter-strand) force model that keeps the hair from
//     collapsing in on itself,
//   * collisions are resolved against the body (ellipsoid union) and the ground,
//   * after position correction a smoothing/relaxation pass minimizes the local
//     deformation injected by collision handling (the paper's second step).
//
// Integration is a Verlet / position-based dynamics scheme which stays stable
// with large time steps and fast wind, just like the implicit solver in the paper.
#pragma once

#include <cstdint>
#include <string>
#include <vector>

#include "math.hpp"

namespace hv {

struct Ellipsoid {
  Vec3 center;
  Vec3 radii;
};

// Signed-distance field of the scanned bust, quantised to 2 mm steps.
//
// Sampling it (instead of a handful of ellipsoids) makes the hair collide with the
// real surface: the nose, brow, cheekbones, ears, jaw and neck all push the strands
// the way they do on a real head.
struct SdfField {
  int nx = 0, ny = 0, nz = 0;
  Vec3 origin{};
  float cell = 0.0f;
  std::vector<int8_t> data;

  bool valid() const { return nx > 0 && !data.empty(); }
  bool load(const std::string& path);
  float sample(const Vec3& p) const;   // signed distance in metres (negative = inside)
  Vec3 gradient(const Vec3& p) const;  // outward direction
};

// Collision proxy for the bust (head, neck, shoulders, chest) + the floor.
struct BodyShape {
  std::vector<Ellipsoid> solids;
  SdfField sdf;
  float groundY = 0.0f;

  Vec3 headCenter{0, 1.60f, 0};
  Vec3 headRadii{0.078f, 0.107f, 0.092f};

  // Keep-out slab in front of the face: the hair parts around it instead of
  // draping over the eyes (a lateral push, like combing the hair behind the ears).
  bool faceGuard = true;
  float guardY0 = 1.435f, guardY1 = 1.660f;
  float guardHalfWidth = 0.064f;
  float guardZ = 0.015f;

  // Projects `p` out of every solid (with `margin` skin thickness) and above the floor.
  // Returns true when the point had to be moved.
  bool resolve(Vec3& p, float margin) const;
  bool inside(const Vec3& p, float margin) const;
};

struct HairVertex {
  float px, py, pz;
  float tx, ty, tz;
  float sideSign;
  float u;
  float rnd;
};

struct WindParams {
  Vec3 dir{1.0f, 0.05f, 0.35f};
  float strength = 1.0f;  // 0 = still air
  float time = 0.0f;      // seconds
  float gustFrequency = 1.0f;
};

struct HairStyle {
  int cols = 180;         // strands around the head
  int rows = 48;          // strands from crown to hairline
  int nodes = 16;         // particles per strand
  float length = 0.44f;   // strand length (m)
  float lengthJitter = 0.07f;
  float ribbonWidth = 0.0030f;
  float shellThickness = 0.014f;  // hair volume above the scalp
  float partCenter = 0.0f;        // azimuth of the centre part (0 = front)
  float waveAmp = 0.010f;
  float waveFreq = 2.3f;
};

class HairMesh {
 public:
  void build(const BodyShape& body, const HairStyle& style);
  void reset();

  // One simulated frame (dt seconds). `iterations` is the constraint solver count.
  void step(float dt, const WindParams& wind, int iterations = 8, int substeps = 2);
  void settle(int steps, float dt, const WindParams& wind);

  // Fills the interleaved ribbon vertex buffer (2 vertices per particle).
  void writeRibbonVertices(std::vector<HairVertex>& out) const;

  size_t strandCount() const { return (size_t)cols_ * (size_t)rows_; }
  size_t vertexCount() const { return strandCount() * (size_t)nodes_ * 2u; }
  size_t indexCount() const { return strandCount() * (size_t)(nodes_ - 1) * 6u; }
  size_t triangleCount() const { return indexCount() / 3u; }
  int nodes() const { return nodes_; }
  int cols() const { return cols_; }
  int rows() const { return rows_; }
  const std::vector<Vec3>& positions() const { return pos_; }
  float strandLength(int strand) const { return totalLen_[(size_t)strand]; }

 private:
  size_t idx(int row, int col, int node) const {
    return (((size_t)row * (size_t)cols_ + (size_t)col) * (size_t)nodes_) + (size_t)node;
  }
  Vec3& P(int row, int col, int node) { return pos_[idx(row, col, node)]; }
  const Vec3& P(int row, int col, int node) const { return pos_[idx(row, col, node)]; }

  void applyForcesAndIntegrate(float dt, const WindParams& wind);
  void solveConstraints(int iterations);
  void integrateVelocity(float dt);

  int cols_ = 0, rows_ = 0, nodes_ = 0;
  BodyShape body_;
  HairStyle style_;

  std::vector<Vec3> pos_;     // current positions
  std::vector<Vec3> prev_;    // previous positions (Verlet)
  std::vector<Vec3> rest_;    // rest pose (used for shape stiffness + reset)
  std::vector<float> restSeg_;      // rest segment length per node index
  std::vector<float> u_;            // 0..1 normalized arc position per node index
  std::vector<float> strandRnd_;    // per strand random value
  std::vector<float> totalLen_;     // per strand length
  std::vector<float> sheetRestH_;   // rest length of the (col -> col+1) sheet link
  std::vector<float> sheetRestV_;   // rest length of the (row -> row+1) sheet link
  float ribbonWidth_ = 0.003f;
};

}  // namespace hv
