#version 460
#extension GL_GOOGLE_include_directive : require
#include "uniforms.glsl"

layout(set = 0, binding = 2) uniform sampler2D texA;

layout(location = 0) in vec2 vUV;
layout(location = 0) out vec4 outColor;

layout(push_constant) uniform Push {
  vec4 p;  // xy = 1/resolution
} pc;

float luma(vec3 c) { return dot(c, vec3(0.299, 0.587, 0.114)); }

vec3 fxaa(vec2 uv) {
  vec2 texel = pc.p.xy;
  vec3 rgbM = texture(texA, uv).rgb;

  vec3 rgbNW = texture(texA, uv + vec2(-1.0, -1.0) * texel).rgb;
  vec3 rgbNE = texture(texA, uv + vec2(1.0, -1.0) * texel).rgb;
  vec3 rgbSW = texture(texA, uv + vec2(-1.0, 1.0) * texel).rgb;
  vec3 rgbSE = texture(texA, uv + vec2(1.0, 1.0) * texel).rgb;

  float lM = luma(rgbM);
  float lNW = luma(rgbNW);
  float lNE = luma(rgbNE);
  float lSW = luma(rgbSW);
  float lSE = luma(rgbSE);

  float lMin = min(lM, min(min(lNW, lNE), min(lSW, lSE)));
  float lMax = max(lM, max(max(lNW, lNE), max(lSW, lSE)));

  // Early out on flat regions so we do not blur clean areas.
  if (lMax - lMin < max(0.0312, lMax * 0.125)) return rgbM;

  vec2 dir;
  dir.x = -((lNW + lNE) - (lSW + lSE));
  dir.y = (lNW + lSW) - (lNE + lSE);
  float dirReduce = max((lNW + lNE + lSW + lSE) * 0.03125, 1.0 / 128.0);
  float rcpDirMin = 1.0 / (min(abs(dir.x), abs(dir.y)) + dirReduce);
  dir = clamp(dir * rcpDirMin, vec2(-8.0), vec2(8.0)) * texel;

  vec3 rgbA = 0.5 * (texture(texA, uv + dir * (1.0 / 3.0 - 0.5)).rgb +
                     texture(texA, uv + dir * (2.0 / 3.0 - 0.5)).rgb);
  vec3 rgbB = rgbA * 0.5 + 0.25 * (texture(texA, uv + dir * -0.5).rgb +
                                   texture(texA, uv + dir * 0.5).rgb);

  float lB = luma(rgbB);
  if (lB < lMin || lB > lMax) return rgbA;
  return rgbB;
}

void main() {
  outColor = vec4(fxaa(vUV), 1.0);
}
