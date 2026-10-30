#version 460
#extension GL_GOOGLE_include_directive : require
#include "lighting.glsl"

layout(location = 0) in vec3 vWorld;
layout(location = 1) in vec3 vTangent;
layout(location = 2) in vec3 vNormal;
layout(location = 3) in float vU;
layout(location = 4) in float vRnd;
layout(location = 5) in vec3 vSide;
layout(location = 6) in float vSideSign;

layout(location = 0) out vec4 outColor;

// Anisotropic hair highlight: sin(theta) lobes along the strand tangent
// (Kajiya-Kay style), with a root->tip shift for the secondary highlight.
float strandLobe(vec3 T, vec3 H, float power) {
  float s = sqrt(max(0.0, 1.0 - dot(T, H) * dot(T, H)));
  return pow(s, power);
}

void main() {
  vec3 T = normalize(vTangent);
  // Rounded cross-section: blend the flat ribbon normal towards the ribbon edge
  // so each strand shades like a cylinder rather than a flat card.
  vec3 N = normalize(normalize(vNormal) * 0.78 + normalize(vSide) * (vSideSign * 0.62));
  vec3 V = normalize(F.camPos.xyz - vWorld);
  vec3 L = normalize(F.sunDir.xyz);
  vec3 H = normalize(L + V);

  // Per-strand colour variation + fine streaks along the strand.
  float strandVar = 1.0 + 0.20 * (fract(vRnd * 91.7) - 0.5);
  float streak = fbm(vec2(vU * 3.2 + vRnd * 37.0, vRnd * 11.0), 3);
  vec3 base = F.hairColor.rgb * strandVar * (0.82 + 0.36 * streak);

  // Root-to-tip darkening (roots sit in the shadow of the hair mass).
  float rootShade = mix(0.55, 1.0, smoothstep(0.0, 0.30, vU));
  // Cheap ambient occlusion from the mass around the head.
  float massAo = mix(0.42, 1.0, smoothstep(0.05, 0.75, vU));

  float shadow = mix(1.0, mix(0.28, 1.0, shadowFactor(vWorld, N, 0.0018)), F.params2.x);

  // --- diffuse (Kajiya-Kay) ---------------------------------------------------
  float sinTL = sqrt(max(0.0, 1.0 - dot(T, L) * dot(T, L)));
  float diff = 0.35 + 0.65 * sinTL;
  vec3 sun = F.sunColor.rgb * F.sunDir.w;

  // --- specular: two shifted anisotropic lobes ---------------------------------
  vec3 T1 = normalize(T + N * 0.10);
  vec3 T2 = normalize(T - N * 0.22);
  float spec1 = strandLobe(T1, H, 420.0) * 0.42;
  float spec2 = strandLobe(T2, H, 120.0) * 0.10;
  float fres = pow(1.0 - saturate(dot(N, V)), 3.0);
  float shimmer = 0.80 + 0.30 * streak;

  vec3 sheen = mix(F.hairColor.rgb * 4.0, vec3(1.0), 0.42);
  vec3 specCol = sheen * spec1 + vec3(0.95, 0.70, 0.45) * spec2;
  specCol *= F.hairColor.w * shimmer * (0.35 + 0.65 * fres) * shadow;
  specCol += skyAmbient(reflect(-V, N)) * (0.02 + 0.09 * fres) * streak;

  // Ambient from the sky, modulated by the mass occlusion.
  vec3 ambient = skyAmbient(N) * massAo;

  vec3 color = base * (sun * diff * shadow * 0.78 + ambient * 0.22) * rootShade;
  color += specCol * sun;
  color = applyFog(color, vWorld);

  outColor = vec4(sanitizeHDR(color), 1.0);
}
