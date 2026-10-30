#version 460
#extension GL_GOOGLE_include_directive : require
#include "lighting.glsl"

layout(location = 0) in vec3 vWorld;
layout(location = 1) in vec3 vNormal;
layout(location = 2) in vec2 vUV;

layout(location = 0) out vec4 outColor;

void main() {
  vec3 N = normalize(vNormal);
  vec3 V = normalize(F.camPos.xyz - vWorld);
  vec3 L = normalize(F.sunDir.xyz);
  vec3 H = normalize(L + V);

  // Mannequin skin with subtle mottling so the surface does not look like plastic.
  float mottle = fbm(vWorld.xz * 42.0 + vWorld.y * 17.0, 3);
  vec3 albedo = vec3(0.300, 0.166, 0.104) * (0.90 + 0.20 * mottle);
  // Slightly warmer/darker in creases (cheap cavity approximation from the normal).
  albedo *= 0.94 + 0.06 * saturate(N.y);

  // Painted facial features in head-local space (the reference bust has a
  // smooth mannequin face, so the features are shading only).
  vec3 lc = (vWorld - vec3(0.0, 1.60, 0.0)) / vec3(0.081, 0.110, 0.095);
  float frontMask = smoothstep(0.15, 0.45, lc.z);
  float eyeMask = exp(-pow((abs(lc.x) - 0.30) / 0.14, 2.0)) * exp(-pow((lc.y - 0.05) / 0.10, 2.0));
  eyeMask *= frontMask;
  float browMask = exp(-pow((abs(lc.x) - 0.29) / 0.19, 2.0)) * exp(-pow((lc.y - 0.19) / 0.055, 2.0));
  browMask *= frontMask;
  float lipMask = exp(-(lc.x * lc.x) / 0.022) * exp(-pow((lc.y + 0.45) / 0.055, 2.0));
  lipMask *= frontMask;
  float nostril = exp(-pow((abs(lc.x) - 0.09) / 0.05, 2.0)) * exp(-pow((lc.y + 0.31) / 0.045, 2.0));
  nostril *= frontMask;

  albedo = mix(albedo, vec3(0.055, 0.042, 0.038), saturate(eyeMask * 0.85));
  albedo = mix(albedo, vec3(0.085, 0.052, 0.036), saturate(browMask * 0.45));
  albedo = mix(albedo, vec3(0.300, 0.125, 0.098), saturate(lipMask * 0.65));
  albedo = mix(albedo, vec3(0.070, 0.045, 0.035), saturate(nostril * 0.6));

  // Softened sun term: shadowed skin still receives a little scattered light so
  // the terminator does not crush to black.
  float sh = mix(0.42, 1.0, shadowFactor(vWorld, N, 0.0022));
  float ndl = max(dot(N, L), 0.0);
  float wrapped = saturate((dot(N, L) + 0.42) / 1.42);

  // Subsurface-ish response: red bleeds through the terminator.
  vec3 sss = vec3(0.055, 0.016, 0.008) * pow(wrapped, 3.0);

  vec3 sun = F.sunColor.rgb * F.sunDir.w;
  vec3 amb = skyAmbient(N);

  // Ambient occlusion around the collar / under the jaw.
  float ao = 0.72 + 0.28 * saturate(N.y * 0.5 + 0.5);
  ao *= mix(0.75, 1.0, sh);

  vec3 color = albedo * (sun * (wrapped * sh) + amb * ao * 1.20) + sss * sh * F.sunDir.w;

  // Skin specular (broad, low intensity) + a little sheen.
  float rough = 0.42;
  float a2 = rough * rough * rough * rough;
  float ndh = max(dot(N, H), 0.0);
  float d = ndh * ndh * (a2 - 1.0) + 1.0;
  float ggx = a2 / (PI * d * d);
  float ndv = max(dot(N, V), 1e-4);
  float fres = 0.03 + 0.97 * pow(1.0 - ndv, 5.0);
  float vis = 0.5 / max(ndv, 0.2);
  color += sun * sh * ggx * fres * vis * ndl * 0.55;
  color += skyAmbient(reflect(-V, N)) * fres * 0.10;

  color = applyFog(color, vWorld);
  outColor = vec4(sanitizeHDR(color), 1.0);
}
