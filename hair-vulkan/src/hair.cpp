#include "hair.hpp"

#include <algorithm>
#include <cmath>

namespace hv {
namespace {

inline Vec3 ellipsoidPoint(const Vec3& c, const Vec3& r, float phi, float theta) {
  return {c.x + r.x * std::sin(phi) * std::sin(theta), c.y + r.y * std::cos(phi),
          c.z + r.z * std::sin(phi) * std::cos(theta)};
}

inline Vec3 ellipsoidNormal(const Vec3& c, const Vec3& r, const Vec3& p) {
  return normalize(Vec3((p.x - c.x) / (r.x * r.x), (p.y - c.y) / (r.y * r.y),
                        (p.z - c.z) / (r.z * r.z)));
}

// Background wind field: a slowly rotating, gusty direction with a bit of swirl.
inline Vec3 windAt(float time, float freq, uint32_t seed) {
  float a = fbm1(time * 0.35f * freq, seed) * 6.2831f;
  float b = fbm1(time * 0.21f * freq + 11.0f, seed + 77u) * 6.2831f;
  Vec3 v(std::cos(a) * std::cos(b), std::sin(b) * 0.55f, std::sin(a) * std::cos(b));
  return normalize(v + Vec3(0.0f, 0.15f, 0.0f));
}

}  // namespace

bool BodyShape::resolve(Vec3& p, float margin) const {
  bool moved = false;
  for (int pass = 0; pass < 2; ++pass) {
    for (const Ellipsoid& e : solids) {
      Vec3 d = p - e.center;
      Vec3 q(d.x / e.radii.x, d.y / e.radii.y, d.z / e.radii.z);
      float r = length(q);
      if (r < 1.0f) {
        Vec3 qn = r > 1e-5f ? q / r : Vec3(0.0f, 0.0f, 1.0f);
        float s = 1.0f + margin / std::min({e.radii.x, e.radii.y, e.radii.z});
        Vec3 q2 = qn * s;
        p = e.center + Vec3(q2.x * e.radii.x, q2.y * e.radii.y, q2.z * e.radii.z);
        moved = true;
      }
    }
    if (p.y < groundY + margin) {
      p.y = groundY + margin;
      moved = true;
    }
  }
  return moved;
}

bool BodyShape::inside(const Vec3& p, float margin) const {
  for (const Ellipsoid& e : solids) {
    Vec3 d = p - e.center;
    Vec3 q(d.x / (e.radii.x + margin), d.y / (e.radii.y + margin), d.z / (e.radii.z + margin));
    if (length(q) < 1.0f) return true;
  }
  return p.y < groundY + margin;
}

void HairMesh::build(const BodyShape& body, const HairStyle& style) {
  body_ = body;
  style_ = style;
  cols_ = std::max(8, style.cols);
  rows_ = std::max(4, style.rows);
  nodes_ = std::max(4, style.nodes);
  ribbonWidth_ = style.ribbonWidth;

  const size_t count = (size_t)cols_ * (size_t)rows_ * (size_t)nodes_;
  pos_.assign(count, Vec3());
  prev_.assign(count, Vec3());
  rest_.assign(count, Vec3());
  restSeg_.assign((size_t)nodes_, 0.0f);
  u_.assign((size_t)nodes_, 0.0f);
  strandRnd_.assign((size_t)cols_ * (size_t)rows_, 0.0f);
  totalLen_.assign((size_t)cols_ * (size_t)rows_, 0.0f);
  sheetRestH_.assign(count, 0.0f);
  sheetRestV_.assign(count, 0.0f);

  for (int i = 0; i < nodes_; ++i) u_[(size_t)i] = (float)i / (float)(nodes_ - 1);

  const Vec3 hc = body_.headCenter;
  const Vec3 hr = body_.headRadii;

  for (int row = 0; row < rows_; ++row) {
    for (int col = 0; col < cols_; ++col) {
      const int strand = row * cols_ + col;
      const uint32_t seed = 0x9e3779b9u * (uint32_t)(strand + 1) + 12345u;
      const float rnd = rand01(seed);
      strandRnd_[(size_t)strand] = rnd;

      const float colF = ((float)col + 0.5f) / (float)cols_;
      const float rowF = ((float)row + 0.5f) / (float)rows_;

      // Azimuth: 0 at the front (+z), +-pi at the back; the centre part sits at theta = 0.
      const float theta = (colF * 2.0f - 1.0f) * PI;
      const float cosT = std::cos(theta);

      // Hairline: high over the forehead, low at the nape.
      const float frontness = smoothstepf(-0.35f, 0.95f, cosT);
      float phiMax = lerpf(1.50f, 0.84f, frontness);
      phiMax += 0.035f * randSigned(seed ^ 0x51u);  // natural hairline wobble

      const float phi = phiMax * std::pow(rowF, 0.80f);

      Vec3 root = ellipsoidPoint(hc, hr * 0.982f, phi, theta);
      root += Vec3(randSigned(seed + 3u) * 0.0012f, 0.0f, randSigned(seed + 5u) * 0.0012f);

      // Frame at the root.
      const Vec3 n = ellipsoidNormal(hc, hr, root);
      const Vec3 down(0.0f, -1.0f, 0.0f);
      Vec3 tangentialDown = down - n * dot(down, n);
      if (length2(tangentialDown) < 1e-8f) tangentialDown = Vec3(0, 0, -1) - n * dot(Vec3(0, 0, -1), n);
      tangentialDown = normalize(tangentialDown);

      // Centre part: comb the hair sideways away from theta = 0 over the front.
      const float side = (theta >= 0.0f) ? 1.0f : -1.0f;
      Vec3 partSweep = Vec3(side, 0.0f, 0.0f);
      partSweep = partSweep - n * dot(partSweep, n);
      partSweep = normalize(partSweep);
      const float partness = frontness * std::exp(-(theta * theta) / (1.30f * 1.30f));

      // Coherent 'locks': neighbouring strands share a bulge so the hair reads as
      // clumped sheets instead of a perfectly smooth shell.
      const float lock = fbm1((float)col * 0.11f + (float)row * 0.028f, 0x5eedu);

      // Strand length with a gently varied cut line.
      const float lenVar = 1.0f + style.lengthJitter * (fbm1((float)col * 0.11f + (float)row * 0.07f, seed) * 2.0f - 1.0f);
      const float totalLen = style.length * lenVar;
      const float segLen = totalLen / (float)(nodes_ - 1);
      totalLen_[(size_t)strand] = totalLen;

      // Strong sideways comb away from the centre part so the face stays visible.
      Vec3 dir = normalize(tangentialDown * (0.55f - 0.32f * partness) +
                           partSweep * (partness * 1.75f) + n * 0.13f);
      Vec3 p = root;

      for (int node = 0; node < nodes_; ++node) {
        const size_t id = idx(row, col, node);
        pos_[id] = p;
        prev_[id] = p;
        rest_[id] = p;
        restSeg_[(size_t)node] = segLen;

        if (node == nodes_ - 1) break;

        const float t = (float)node / (float)(nodes_ - 1);
        // Radial (outward) direction around the head axis, used to give the hair volume.
        Vec3 radial(p.x - hc.x, 0.0f, p.z - hc.z);
        if (length2(radial) < 1e-8f) radial = Vec3(0, 0, 1);
        radial = normalize(radial);

        const float flare = smoothstepf(0.02f, 0.40f, t) * (0.62f + 0.78f * lock) * (0.92f + 0.16f * rnd);
        const float inward = smoothstepf(0.68f, 1.0f, t) * 0.20f;  // tips curl back towards the body
        const float sweepFade = std::pow(1.0f - t, 1.5f);
        Vec3 target = normalize(Vec3(0.0f, -1.0f, 0.0f) + radial * (0.15f * flare - inward) +
                                partSweep * (partness * 1.10f * sweepFade) +
                                tangentialDown * (0.22f * sweepFade));
        dir = normalize(lerp(dir, target, 0.20f + 0.55f * t));
        p = p + dir * segLen;

        // A little life: a damped sine wave towards the tips.
        const float phase = rnd * 6.2831f;
        const float wave = std::sin(t * style.waveFreq * 6.2831f + phase) * style.waveAmp *
                           smoothstepf(0.25f, 1.0f, t);
        Vec3 sideDir = normalize(cross(dir, radial) + Vec3(1e-4f, 0.0f, 0.0f));
        p = p + sideDir * wave;

        body_.resolve(p, 0.004f);
        if (p.y < hc.y - 0.02f) body_.resolve(p, 0.006f);
      }
    }
  }

  // Rest lengths for the hair-mesh sheet links.
  for (int row = 0; row < rows_; ++row) {
    for (int col = 0; col < cols_; ++col) {
      const int nextCol = (col + 1) % cols_;
      for (int node = 0; node < nodes_; ++node) {
        const size_t a = idx(row, col, node);
        const size_t b = idx(row, nextCol, node);
        sheetRestH_[a] = std::max(1e-4f, length(pos_[b] - pos_[a]));
        const int nextRow = std::min(row + 1, rows_ - 1);
        const size_t c = idx(nextRow, col, node);
        sheetRestV_[a] = std::max(1e-4f, length(pos_[c] - pos_[a]));
      }
    }
  }
}

void HairMesh::reset() {
  pos_ = rest_;
  prev_ = rest_;
}

void HairMesh::applyForcesAndIntegrate(float dt, const WindParams& wind) {
  const float damping = 0.975f;
  const float gravity = -9.81f;
  const float dt2 = dt * dt;

  const Vec3 baseWind = normalize(wind.dir);
  const float gust = 0.65f + 0.75f * fbm1(wind.time * 0.9f * wind.gustFrequency, 0x51ed270bu);

  for (int row = 0; row < rows_; ++row) {
    for (int col = 0; col < cols_; ++col) {
      const int strand = row * cols_ + col;
      const uint32_t seed = 0x9e3779b9u * (uint32_t)(strand + 1) + 12345u;
      const float rnd = strandRnd_[(size_t)strand];
      // Per-strand wind variation so the sheet ripples instead of moving as a block.
      const float localGust =
          0.55f + 0.9f * fbm1(wind.time * 2.1f * wind.gustFrequency + rnd * 9.0f, seed ^ 0xabcdefu);
      const Vec3 localDir = normalize(baseWind + Vec3(randSigned(seed) * 0.35f,
                                                      randSigned(seed + 1u) * 0.15f,
                                                      randSigned(seed + 2u) * 0.35f));
      for (int node = 1; node < nodes_; ++node) {
        const size_t id = idx(row, col, node);
        const float u = u_[(size_t)node];
        Vec3 vel = (pos_[id] - prev_[id]) * damping;
        prev_[id] = pos_[id];

        const float exposure = 0.25f + 0.75f * u;
        const float flutter = std::sin(wind.time * 7.0f + rnd * 6.2831f + u * 4.5f) * 0.25f;
        Vec3 accel(0.0f, gravity, 0.0f);
        // Wind acceleration is intentionally mild: hair is light, but a hair *mesh*
        // is braced by its sheet links, so the style must survive the gusts.
        accel += localDir * (wind.strength * gust * localGust * exposure * 5.2f);
        accel += Vec3(0.0f, flutter * wind.strength * 1.5f, 0.0f);
        pos_[id] = pos_[id] + vel + accel * dt2;
      }
    }
  }
}

void HairMesh::solveConstraints(int iterations) {
  const float bodyMargin = 0.006f;
  for (int it = 0; it < iterations; ++it) {
    // 1) Root stiffness: the hair keeps its style close to the scalp.
    for (int row = 0; row < rows_; ++row) {
      for (int col = 0; col < cols_; ++col) {
        for (int node = 1; node < 5 && node < nodes_; ++node) {
          const size_t id = idx(row, col, node);
          const float u = u_[(size_t)node];
          const float k = 0.42f * (1.0f - u) * (1.0f - u);
          pos_[id] = lerp(pos_[id], rest_[id], k);
        }
      }
    }

    // 2) Strand distance constraints.
    for (int row = 0; row < rows_; ++row) {
      for (int col = 0; col < cols_; ++col) {
        for (int node = 1; node < nodes_; ++node) {
          const size_t a = idx(row, col, node - 1);
          const size_t b = idx(row, col, node);
          Vec3 d = pos_[b] - pos_[a];
          float len = length(d);
          float rest = restSeg_[(size_t)node];
          if (len < 1e-7f) continue;
          float diff = (len - rest) / len;
          if (node == 1) {
            // The root particle is pinned.
            pos_[b] = pos_[b] - d * diff;
          } else {
            pos_[a] = pos_[a] + d * (diff * 0.5f);
            pos_[b] = pos_[b] - d * (diff * 0.5f);
          }
        }
      }
    }

    // 3) Bending constraints (soft): keeps the strands smooth.
    for (int row = 0; row < rows_; ++row) {
      for (int col = 0; col < cols_; ++col) {
        for (int node = 2; node < nodes_; ++node) {
          const size_t a = idx(row, col, node - 2);
          const size_t b = idx(row, col, node);
          Vec3 d = pos_[b] - pos_[a];
          float len = length(d);
          float rest = restSeg_[(size_t)(node - 1)] + restSeg_[(size_t)node];
          if (len < 1e-7f) continue;
          float diff = (len - rest) / len * 0.22f;
          if (node - 2 == 0) {
            pos_[b] = pos_[b] - d * diff;
          } else {
            pos_[a] = pos_[a] + d * (diff * 0.5f);
            pos_[b] = pos_[b] - d * (diff * 0.5f);
          }
        }
      }
    }

    // 4) Hair-mesh sheet links: neighbouring strands keep a coherent sheet, and a
    //    minimum-distance term acts as the volumetric (inter-strand) force.
    for (int row = 0; row < rows_; ++row) {
      for (int col = 0; col < cols_; ++col) {
        const int nextCol = (col + 1) % cols_;
        for (int node = 0; node < nodes_; ++node) {
          const size_t a = idx(row, col, node);
          const size_t b = idx(row, nextCol, node);
          Vec3 d = pos_[b] - pos_[a];
          float len = length(d);
          if (len < 1e-7f) continue;
          const float rest = sheetRestH_[a];
          float k = 0.32f;
          float target = rest;
          if (len < rest * 0.62f) {  // compression: resist volume collapse
            k = 0.45f;
            target = rest * 0.62f;
          }
          float diff = (len - target) / len * k;
          bool pinA = (node == 0 && (row == 0 || row == rows_ - 1));
          pos_[a] = pos_[a] + d * (diff * 0.5f);
          pos_[b] = pos_[b] - d * (diff * 0.5f);
          if (pinA) {
            pos_[a] = rest_[a];
          }
        }
      }
    }

    for (int row = 0; row + 1 < rows_; ++row) {
      for (int col = 0; col < cols_; ++col) {
        for (int node = 1; node < nodes_; ++node) {
          const size_t a = idx(row, col, node);
          const size_t b = idx(row + 1, col, node);
          Vec3 d = pos_[b] - pos_[a];
          float len = length(d);
          if (len < 1e-7f) continue;
          const float rest = sheetRestV_[a];
          const float k = (len < rest * 0.62f) ? 0.40f : 0.22f;
          const float target = (len < rest * 0.62f) ? rest * 0.62f : rest;
          float diff = (len - target) / len * k;
          pos_[a] = pos_[a] + d * (diff * 0.5f);
          pos_[b] = pos_[b] - d * (diff * 0.5f);
        }
      }
    }

    // 5) Collision handling + relaxation that removes the deformation injected by
    //    the position corrections (the second step of the paper's collision pass).
    for (int row = 0; row < rows_; ++row) {
      for (int col = 0; col < cols_; ++col) {
        for (int node = 0; node < nodes_; ++node) {
          const size_t id = idx(row, col, node);
          if (node == 0) {
            pos_[id] = rest_[id];  // roots stay on the scalp
            continue;
          }
          body_.resolve(pos_[id], bodyMargin);
        }
      }
    }
    if (it == iterations - 1) {
      // Laplacian smoothing along strands (minimizes local deformation).
      for (int row = 0; row < rows_; ++row) {
        for (int col = 0; col < cols_; ++col) {
          for (int node = 1; node + 1 < nodes_; ++node) {
            const size_t a = idx(row, col, node - 1);
            const size_t b = idx(row, col, node);
            const size_t c = idx(row, col, node + 1);
            Vec3 avg = (pos_[a] + pos_[c]) * 0.5f;
            pos_[b] = lerp(pos_[b], avg, 0.035f);
          }
          // Re-pin roots and re-project after smoothing.
          pos_[idx(row, col, 0)] = rest_[idx(row, col, 0)];
        }
      }
      for (int row = 0; row < rows_; ++row) {
        for (int col = 0; col < cols_; ++col) {
          for (int node = 1; node < nodes_; ++node) {
            body_.resolve(pos_[idx(row, col, node)], bodyMargin);
          }
        }
      }
    }
  }
}

void HairMesh::integrateVelocity(float dt) { (void)dt; }

void HairMesh::step(float dt, const WindParams& wind, int iterations, int substeps) {
  if (dt <= 0.0f) return;
  const int sub = std::max(1, substeps);
  const float h = dt / (float)sub;
  for (int s = 0; s < sub; ++s) {
    applyForcesAndIntegrate(h, wind);
    solveConstraints(iterations);
  }
}

void HairMesh::settle(int steps, float dt, const WindParams& wind) {
  for (int i = 0; i < steps; ++i) {
    WindParams w = wind;
    w.time = wind.time + (float)i * dt;
    step(dt, w, 6, 2);
  }
}

void HairMesh::writeRibbonVertices(std::vector<HairVertex>& out) const {
  out.resize(vertexCount());
  size_t o = 0;
  for (int row = 0; row < rows_; ++row) {
    for (int col = 0; col < cols_; ++col) {
      const int strand = row * cols_ + col;
      const float rnd = strandRnd_[(size_t)strand];
      for (int node = 0; node < nodes_; ++node) {
        const size_t id = idx(row, col, node);
        Vec3 p = pos_[id];
        const size_t iPrev = idx(row, col, std::max(0, node - 1));
        const size_t iNext = idx(row, col, std::min(nodes_ - 1, node + 1));
        Vec3 tangent = pos_[iNext] - pos_[iPrev];
        if (length2(tangent) < 1e-12f) tangent = Vec3(0, -1, 0);
        tangent = normalize(tangent);
        // Per-strand tangent jitter: neighbouring strands get slightly different
        // shading tangents, which breaks the specular band into sparkles instead
        // of a single sheet-wide highlight.
        {
          Vec3 helper = std::fabs(tangent.y) < 0.9f ? Vec3(0, 1, 0) : Vec3(1, 0, 0);
          Vec3 b1 = normalize(cross(tangent, helper));
          Vec3 b2 = cross(tangent, b1);
          const float a1 = randSigned((uint32_t)strand * 7u + 11u) * 0.16f;
          const float a2 = randSigned((uint32_t)strand * 13u + 29u) * 0.16f;
          tangent = normalize(tangent + b1 * a1 + b2 * a2);
        }
        const float u = u_[(size_t)node];
        for (int s = 0; s < 2; ++s) {
          HairVertex& v = out[o++];
          v.px = p.x; v.py = p.y; v.pz = p.z;
          v.tx = tangent.x; v.ty = tangent.y; v.tz = tangent.z;
          v.sideSign = (s == 0) ? -1.0f : 1.0f;
          v.u = u;
          v.rnd = rnd;
        }
      }
    }
  }
}

}  // namespace hv
