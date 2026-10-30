#version 460
#extension GL_GOOGLE_include_directive : require
#include "lighting.glsl"

layout(location = 0) in vec2 vUV;
layout(location = 0) out vec4 outColor;

void main() {
  vec3 rd = rayDirection(vUV);
  if (rd.y > -1e-4) discard;  // above the horizon: the sky pass already covered it

  float t = -F.camPos.y / rd.y;
  if (t <= 0.0) discard;
  vec3 P = F.camPos.xyz + rd * t;

  // Exact depth for the reconstructed plane point.
  vec4 clip = F.viewProj * vec4(P, 1.0);
  gl_FragDepth = clip.z / clip.w;

  vec3 N = vec3(0.0, 1.0, 0.0);
  vec3 L = normalize(F.sunDir.xyz);

  // --- asphalt -----------------------------------------------------------------
  float patches = fbm(P.xz * 1.35, 4);
  float grain = fbm(P.xz * 22.0, 3);
  float speck = hash12(floor(P.xz * 620.0));
  float cracks = smoothstep(0.72, 0.80, fbm(P.xz * 0.9 + 5.0, 3));

  float dirt = fbm(P.xz * 1.10 + 31.0, 4);
  vec3 albedo = mix(vec3(0.034, 0.033, 0.032), vec3(0.092, 0.086, 0.079), patches * 0.80 + grain * 0.34);
  albedo *= 0.86 + 0.30 * speck;
  albedo = mix(albedo, albedo * vec3(1.14, 1.08, 0.94), smoothstep(0.50, 0.90, dirt) * 0.45);
  albedo = mix(albedo, vec3(0.022, 0.022, 0.023), cracks * 0.55);

  // Gentle surface relief so the ground is not perfectly flat.
  vec3 Np = normalize(vec3((grain - 0.5) * 0.06, 1.0, (patches - 0.5) * 0.06));

  float sh = shadowFactor(P, N, 0.0015);
  float ndl = max(dot(Np, L), 0.0);
  vec3 sun = F.sunColor.rgb * F.sunDir.w * ndl * sh;

  // Ambient occlusion in the contact region of the bust.
  float radial = length(P.xz);
  float contactAo = mix(0.35, 1.0, smoothstep(0.02, 0.55, radial));
  vec3 amb = skyAmbient(Np) * contactAo;

  vec3 V = -rd;
  vec3 H = normalize(L + V);
  float spec = pow(max(dot(Np, H), 0.0), 110.0) * 0.025;
  vec3 specColor = F.sunColor.rgb * spec * F.sunDir.w * sh;

  vec3 color = albedo * (sun + amb) + specColor;
  color = applyFog(color, P);

  outColor = vec4(sanitizeHDR(color), 1.0);
}
