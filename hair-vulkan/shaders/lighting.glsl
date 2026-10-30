// Procedural sky, sun shadow lookup and small noise helpers shared by the
// scene shaders. No textures are used anywhere: the "png-free" requirement.
#ifndef LIGHTING_GLSL
#define LIGHTING_GLSL

#include "uniforms.glsl"

layout(set = 0, binding = 1) uniform sampler2DShadow shadowMap;
layout(set = 0, binding = 2) uniform sampler2D texA;
layout(set = 0, binding = 3) uniform sampler2D texB;

// ---------------------------------------------------------------- hashing ---
float hash12(vec2 p) {
  vec3 p3 = fract(vec3(p.xyx) * 0.1031);
  p3 += dot(p3, p3.yzx + 33.33);
  return fract((p3.x + p3.y) * p3.z);
}

float valueNoise(vec2 p) {
  vec2 i = floor(p);
  vec2 f = fract(p);
  f = f * f * (3.0 - 2.0 * f);
  float a = hash12(i);
  float b = hash12(i + vec2(1.0, 0.0));
  float c = hash12(i + vec2(0.0, 1.0));
  float d = hash12(i + vec2(1.0, 1.0));
  return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
}

float fbm(vec2 p, int octaves) {
  float sum = 0.0;
  float amp = 0.5;
  float norm = 0.0;
  mat2 rot = mat2(0.8, 0.6, -0.6, 0.8);
  for (int i = 0; i < octaves; ++i) {
    sum += valueNoise(p) * amp;
    norm += amp;
    p = rot * p * 2.03;
    amp *= 0.5;
  }
  return sum / norm;
}

// ------------------------------------------------------------------- sky ---
vec3 skyRadiance(vec3 dir) {
  vec3 sun = normalize(F.sunDir.xyz);
  float y = dir.y;

  // Sky gradient: deep blue zenith, pale warm horizon.
  float horizon = pow(1.0 - clamp(y, 0.0, 1.0), 3.2);
  vec3 zenithColor = vec3(0.042, 0.115, 0.360);
  vec3 midColor = vec3(0.150, 0.320, 0.640);
  vec3 horizonColor = vec3(0.560, 0.660, 0.810);
  vec3 col = mix(zenithColor, midColor, smoothstep(0.0, 0.35, y));
  col = mix(col, horizonColor, horizon);
  col *= F.skyParams.z;

  // Mie-ish glow around the sun plus the solar disk.
  float cosA = max(dot(dir, sun), 0.0);
  vec3 sunTint = F.sunColor.rgb;
  col += sunTint * pow(cosA, 6.0) * 0.16 * F.sunDir.w;
  col += sunTint * pow(cosA, 64.0) * 0.55 * F.sunDir.w;
  float disk = smoothstep(0.99955, 0.99985, cosA);
  col += sunTint * disk * 22.0 * F.sunDir.w * F.skyParams.x;

  // Thin wind-blown cirrus, projected onto a virtual cloud plane.
  if (y > 0.005) {
    vec2 cuv = dir.xz / max(y, 0.02) * 0.55;
    cuv += vec2(F.camPos.w * 0.006, F.camPos.w * 0.002);
    float n = fbm(cuv * 1.6, 5);
    float n2 = fbm(cuv * 4.1 + 17.0, 3);
    float cover = 0.70 - 0.34 * F.skyParams.y;
    float cloud = smoothstep(cover, cover + 0.26, n * 0.70 + n2 * 0.30);
    cloud *= smoothstep(0.0, 0.25, y);
    cloud *= 0.72;
    vec3 cloudLit = mix(vec3(0.42, 0.46, 0.54), vec3(0.95, 0.93, 0.90), pow(cosA, 3.0));
    col = mix(col, cloudLit * (0.45 + 0.30 * F.sunDir.w), cloud);
  }

  // Horizon haze band.
  float haze = exp(-max(y, 0.0) * 16.0) * F.skyParams.w;
  col = mix(col, mix(horizonColor, sunTint, 0.25) * F.skyParams.z, clamp(haze, 0.0, 0.85));

  // Ground bounce below the horizon (only seen in reflections / fog).
  if (y < 0.0) {
    col = mix(col, vec3(0.12, 0.115, 0.105), saturate(-y * 3.0));
  }
  return max(col, vec3(0.0));
}

// Cheap hemispherical irradiance used for ambient lighting.
vec3 skyAmbient(vec3 n) {
  vec3 up = skyRadiance(vec3(0.0, 1.0, 0.0));
  vec3 side = skyRadiance(normalize(vec3(n.x, 0.25, n.z)));
  vec3 bounce = vec3(0.10, 0.095, 0.085);
  float t = saturate(n.y * 0.5 + 0.5);
  // Scale radiance into a usable irradiance and add a small ground-bounce fill
  // so that shadowed skin/hair never crushes to black.
  return mix(bounce, mix(side, up, t), t) * (3.20 * F.sunColor.w) + bounce * 0.45 * F.sunColor.w;
}

// ---------------------------------------------------------------- shadow ---
float lookupShadow(vec2 uv, float ref) {
  return texture(shadowMap, vec3(uv, ref));
}

// 3x3 hardware-PCF shadow lookup with normal offset.
float shadowFactor(vec3 worldPos, vec3 normal, float normalBias) {
  vec4 lp = F.lightViewProj * vec4(worldPos + normal * normalBias, 1.0);
  vec2 uv = lp.xy * 0.5 + 0.5;
  if (uv.x < 0.002 || uv.x > 0.998 || uv.y < 0.002 || uv.y > 0.998) return 1.0;
  // Fade the shadow out towards the shadow-map border so the light frustum does
  // not leave a hard rectangular edge on the ground.
  vec2 edge = smoothstep(vec2(0.0), vec2(0.10), uv) * smoothstep(vec2(0.0), vec2(0.10), 1.0 - uv);
  float ref = lp.z - F.shadowParams.y;
  // 4x4 rotated-grid PCF for soft penumbrae without banding.
  float radius = 3.2 * F.shadowParams.x;
  float sum = 0.0;
  for (int i = 0; i < 4; ++i) {
    for (int j = 0; j < 4; ++j) {
      vec2 o = (vec2(float(i), float(j)) - 1.5) * 0.6666;
      float angle = 0.4636;
      vec2 r = vec2(o.x * cos(angle) - o.y * sin(angle), o.x * sin(angle) + o.y * cos(angle));
      sum += lookupShadow(uv + r * radius, ref);
    }
  }
  float s = sum / 16.0;
  return mix(1.0, s, edge.x * edge.y);
}

// Exponential height fog towards the sky colour.
vec3 applyFog(vec3 color, vec3 worldPos) {
  vec3 v = worldPos - F.camPos.xyz;
  float dist = length(v);
  vec3 dir = v / max(dist, 1e-4);
  float density = F.params.w;
  float amount = 1.0 - exp(-dist * density);
  amount *= mix(0.55, 1.0, saturate(1.0 - abs(dir.y)));
  vec3 fogColor = skyRadiance(normalize(vec3(dir.x, max(dir.y, 0.02), dir.z)));
  return mix(color, fogColor, saturate(amount));
}

#endif
