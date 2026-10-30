// hair-vulkan: tiny linear algebra + noise helpers (no external dependencies).
#pragma once

#include <cmath>
#include <cstddef>
#include <cstdint>
#include <algorithm>

namespace hv {

constexpr float PI = 3.14159265358979323846f;
constexpr float DEG = PI / 180.0f;

inline float clampf(float v, float lo, float hi) { return v < lo ? lo : (v > hi ? hi : v); }
inline float saturate(float v) { return clampf(v, 0.0f, 1.0f); }
inline float lerpf(float a, float b, float t) { return a + (b - a) * t; }
inline float smoothstepf(float e0, float e1, float x) {
  float t = saturate((x - e0) / (e1 - e0 + 1e-20f));
  return t * t * (3.0f - 2.0f * t);
}

struct Vec2 {
  float x = 0, y = 0;
  Vec2() = default;
  Vec2(float x_, float y_) : x(x_), y(y_) {}
};

struct Vec3 {
  float x = 0, y = 0, z = 0;
  Vec3() = default;
  Vec3(float x_, float y_, float z_) : x(x_), y(y_), z(z_) {}
  explicit Vec3(float s) : x(s), y(s), z(s) {}

  Vec3 operator+(const Vec3& o) const { return {x + o.x, y + o.y, z + o.z}; }
  Vec3 operator-(const Vec3& o) const { return {x - o.x, y - o.y, z - o.z}; }
  Vec3 operator*(float s) const { return {x * s, y * s, z * s}; }
  Vec3 operator*(const Vec3& o) const { return {x * o.x, y * o.y, z * o.z}; }
  Vec3 operator/(float s) const { float i = 1.0f / s; return {x * i, y * i, z * i}; }
  Vec3 operator-() const { return {-x, -y, -z}; }
  Vec3& operator+=(const Vec3& o) { x += o.x; y += o.y; z += o.z; return *this; }
  Vec3& operator-=(const Vec3& o) { x -= o.x; y -= o.y; z -= o.z; return *this; }
  Vec3& operator*=(float s) { x *= s; y *= s; z *= s; return *this; }
  float operator[](int i) const { return i == 0 ? x : (i == 1 ? y : z); }
  float& operator[](int i) { return i == 0 ? x : (i == 1 ? y : z); }
};

inline Vec3 operator*(float s, const Vec3& v) { return v * s; }
inline float dot(const Vec3& a, const Vec3& b) { return a.x * b.x + a.y * b.y + a.z * b.z; }
inline Vec3 cross(const Vec3& a, const Vec3& b) {
  return {a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x};
}
inline float length2(const Vec3& v) { return dot(v, v); }
inline float length(const Vec3& v) { return std::sqrt(dot(v, v)); }
inline Vec3 normalize(const Vec3& v) {
  float l = length(v);
  return l > 1e-20f ? v * (1.0f / l) : Vec3(0, 1, 0);
}
inline Vec3 lerp(const Vec3& a, const Vec3& b, float t) { return a + (b - a) * t; }
inline Vec3 vmin(const Vec3& a, const Vec3& b) {
  return {std::min(a.x, b.x), std::min(a.y, b.y), std::min(a.z, b.z)};
}
inline Vec3 vmax(const Vec3& a, const Vec3& b) {
  return {std::max(a.x, b.x), std::max(a.y, b.y), std::max(a.z, b.z)};
}
inline Vec3 reflect(const Vec3& i, const Vec3& n) { return i - n * (2.0f * dot(i, n)); }
inline Vec3 vabs(const Vec3& v) { return {std::fabs(v.x), std::fabs(v.y), std::fabs(v.z)}; }

struct Vec4 {
  float x = 0, y = 0, z = 0, w = 0;
  Vec4() = default;
  Vec4(float x_, float y_, float z_, float w_) : x(x_), y(y_), z(z_), w(w_) {}
  Vec4(const Vec3& v, float w_) : x(v.x), y(v.y), z(v.z), w(w_) {}
  Vec3 xyz() const { return {x, y, z}; }
};

// Column-major 4x4 matrix (same memory layout as GLSL / std140).
struct Mat4 {
  float m[16] = {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1};

  static Mat4 identity() { return Mat4(); }

  float& at(int row, int col) { return m[col * 4 + row]; }
  float at(int row, int col) const { return m[col * 4 + row]; }

  Mat4 operator*(const Mat4& o) const {
    Mat4 r;
    for (int c = 0; c < 4; ++c) {
      for (int rw = 0; rw < 4; ++rw) {
        float s = 0;
        for (int k = 0; k < 4; ++k) s += at(rw, k) * o.at(k, c);
        r.at(rw, c) = s;
      }
    }
    return r;
  }

  Vec4 operator*(const Vec4& v) const {
    return {at(0, 0) * v.x + at(0, 1) * v.y + at(0, 2) * v.z + at(0, 3) * v.w,
            at(1, 0) * v.x + at(1, 1) * v.y + at(1, 2) * v.z + at(1, 3) * v.w,
            at(2, 0) * v.x + at(2, 1) * v.y + at(2, 2) * v.z + at(2, 3) * v.w,
            at(3, 0) * v.x + at(3, 1) * v.y + at(3, 2) * v.z + at(3, 3) * v.w};
  }

  Vec3 transformPoint(const Vec3& p) const {
    Vec4 r = (*this) * Vec4(p, 1.0f);
    return r.xyz();
  }

  static Mat4 translate(const Vec3& t) {
    Mat4 r;
    r.at(0, 3) = t.x; r.at(1, 3) = t.y; r.at(2, 3) = t.z;
    return r;
  }

  static Mat4 scale(const Vec3& s) {
    Mat4 r;
    r.at(0, 0) = s.x; r.at(1, 1) = s.y; r.at(2, 2) = s.z;
    return r;
  }

  // Right-handed perspective, Vulkan depth range [0,1]. Y is flipped by the
  // negative-height viewport used when binding the dynamic viewport.
  static Mat4 perspective(float fovyRad, float aspect, float znear, float zfar) {
    Mat4 r;
    float f = 1.0f / std::tan(fovyRad * 0.5f);
    for (int i = 0; i < 16; ++i) r.m[i] = 0.0f;
    r.at(0, 0) = f / aspect;
    r.at(1, 1) = f;  // +Y up; the renderer uses a negative-height viewport (Vulkan NDC is Y-down)
    r.at(2, 2) = zfar / (znear - zfar);
    r.at(2, 3) = zfar * znear / (znear - zfar);
    r.at(3, 2) = -1.0f;
    return r;
  }

  static Mat4 ortho(float l, float r_, float b, float t, float n, float f) {
    Mat4 r;
    for (int i = 0; i < 16; ++i) r.m[i] = 0.0f;
    r.at(0, 0) = 2.0f / (r_ - l);
    r.at(1, 1) = 2.0f / (t - b);
    r.at(2, 2) = -1.0f / (f - n);
    r.at(0, 3) = -(r_ + l) / (r_ - l);
    r.at(1, 3) = -(t + b) / (t - b);
    r.at(2, 3) = -n / (f - n);
    r.at(3, 3) = 1.0f;
    return r;
  }

  static Mat4 lookAt(const Vec3& eye, const Vec3& center, const Vec3& up) {
    Vec3 f = normalize(center - eye);
    Vec3 s = normalize(cross(f, up));
    Vec3 u = cross(s, f);
    Mat4 r;
    r.at(0, 0) = s.x; r.at(0, 1) = s.y; r.at(0, 2) = s.z; r.at(0, 3) = -dot(s, eye);
    r.at(1, 0) = u.x; r.at(1, 1) = u.y; r.at(1, 2) = u.z; r.at(1, 3) = -dot(u, eye);
    r.at(2, 0) = -f.x; r.at(2, 1) = -f.y; r.at(2, 2) = -f.z; r.at(2, 3) = dot(f, eye);
    r.at(3, 0) = 0; r.at(3, 1) = 0; r.at(3, 2) = 0; r.at(3, 3) = 1;
    return r;
  }
};

inline Mat4 inverse(const Mat4& mat) {
  const float* m = mat.m;
  float inv[16];
  inv[0] = m[5] * m[10] * m[15] - m[5] * m[11] * m[14] - m[9] * m[6] * m[15] +
           m[9] * m[7] * m[14] + m[13] * m[6] * m[11] - m[13] * m[7] * m[10];
  inv[4] = -m[4] * m[10] * m[15] + m[4] * m[11] * m[14] + m[8] * m[6] * m[15] -
           m[8] * m[7] * m[14] - m[12] * m[6] * m[11] + m[12] * m[7] * m[10];
  inv[8] = m[4] * m[9] * m[15] - m[4] * m[11] * m[13] - m[8] * m[5] * m[15] +
           m[8] * m[7] * m[13] + m[12] * m[5] * m[11] - m[12] * m[7] * m[9];
  inv[12] = -m[4] * m[9] * m[14] + m[4] * m[10] * m[13] + m[8] * m[5] * m[14] -
            m[8] * m[6] * m[13] - m[12] * m[5] * m[10] + m[12] * m[6] * m[9];
  inv[1] = -m[1] * m[10] * m[15] + m[1] * m[11] * m[14] + m[9] * m[2] * m[15] -
           m[9] * m[3] * m[14] - m[13] * m[2] * m[11] + m[13] * m[3] * m[10];
  inv[5] = m[0] * m[10] * m[15] - m[0] * m[11] * m[14] - m[8] * m[2] * m[15] +
           m[8] * m[3] * m[14] + m[12] * m[2] * m[11] - m[12] * m[3] * m[10];
  inv[9] = -m[0] * m[9] * m[15] + m[0] * m[11] * m[13] + m[8] * m[1] * m[15] -
           m[8] * m[3] * m[13] - m[12] * m[1] * m[11] + m[12] * m[3] * m[9];
  inv[13] = m[0] * m[9] * m[14] - m[0] * m[10] * m[13] - m[8] * m[1] * m[14] +
            m[8] * m[2] * m[13] + m[12] * m[1] * m[10] - m[12] * m[2] * m[9];
  inv[2] = m[1] * m[6] * m[15] - m[1] * m[7] * m[14] - m[5] * m[2] * m[15] +
           m[5] * m[3] * m[14] + m[13] * m[2] * m[7] - m[13] * m[3] * m[6];
  inv[6] = -m[0] * m[6] * m[15] + m[0] * m[7] * m[14] + m[4] * m[2] * m[15] -
           m[4] * m[3] * m[14] - m[12] * m[2] * m[7] + m[12] * m[3] * m[6];
  inv[10] = m[0] * m[5] * m[15] - m[0] * m[7] * m[13] - m[4] * m[1] * m[15] +
            m[4] * m[3] * m[13] + m[12] * m[1] * m[7] - m[12] * m[3] * m[5];
  inv[14] = -m[0] * m[5] * m[14] + m[0] * m[6] * m[13] + m[4] * m[1] * m[14] -
            m[4] * m[2] * m[13] - m[12] * m[1] * m[6] + m[12] * m[2] * m[5];
  inv[3] = -m[1] * m[6] * m[11] + m[1] * m[7] * m[10] + m[5] * m[2] * m[11] -
           m[5] * m[3] * m[10] - m[9] * m[2] * m[7] + m[9] * m[3] * m[6];
  inv[7] = m[0] * m[6] * m[11] - m[0] * m[7] * m[10] - m[4] * m[2] * m[11] +
           m[4] * m[3] * m[10] + m[8] * m[2] * m[7] - m[8] * m[3] * m[6];
  inv[11] = -m[0] * m[5] * m[11] + m[0] * m[7] * m[9] + m[4] * m[1] * m[11] -
            m[4] * m[3] * m[9] - m[8] * m[1] * m[7] + m[8] * m[3] * m[5];
  inv[15] = m[0] * m[5] * m[10] - m[0] * m[6] * m[9] - m[4] * m[1] * m[10] +
            m[4] * m[2] * m[9] + m[8] * m[1] * m[6] - m[8] * m[2] * m[5];

  float det = m[0] * inv[0] + m[1] * inv[4] + m[2] * inv[8] + m[3] * inv[12];
  Mat4 r;
  if (std::fabs(det) < 1e-20f) return Mat4::identity();
  det = 1.0f / det;
  for (int i = 0; i < 16; ++i) r.m[i] = inv[i] * det;
  return r;
}

// ---------------------------------------------------------------------------
// Deterministic hashing / value noise (used for hair variation on the CPU).
// ---------------------------------------------------------------------------
inline uint32_t hashU32(uint32_t x) {
  x ^= x >> 16; x *= 0x7feb352dU; x ^= x >> 15; x *= 0x846ca68bU; x ^= x >> 16;
  return x;
}
inline float hashToUnit(uint32_t h) { return (float)(h & 0x00ffffffU) / 16777215.0f; }
inline float rand01(uint32_t seed) { return hashToUnit(hashU32(seed)); }
inline float randSigned(uint32_t seed) { return rand01(seed) * 2.0f - 1.0f; }

inline float smoothNoise1(float x, uint32_t seed) {
  float fx = std::floor(x);
  float t = x - fx;
  t = t * t * (3.0f - 2.0f * t);
  float a = rand01(seed + (uint32_t)(int)fx * 7919u);
  float b = rand01(seed + ((uint32_t)(int)fx + 1u) * 7919u);
  return lerpf(a, b, t);
}

inline float fbm1(float x, uint32_t seed, int octaves = 4) {
  float sum = 0.0f, amp = 0.5f, freq = 1.0f, norm = 0.0f;
  for (int i = 0; i < octaves; ++i) {
    sum += smoothNoise1(x * freq, seed + i * 131u) * amp;
    norm += amp;
    amp *= 0.5f;
    freq *= 2.0f;
  }
  return sum / norm;
}

}  // namespace hv
