#version 460
#extension GL_GOOGLE_include_directive : require
#include "lighting.glsl"

layout(location = 0) in vec3 vWorld;
layout(location = 1) in vec3 vNormal;
layout(location = 2) in vec3 vLocal;

layout(location = 0) out vec4 outColor;

void main() {
  vec3 N = normalize(vNormal);
  vec3 V = normalize(F.camPos.xyz - vWorld);
  vec3 L = normalize(F.sunDir.xyz);
  vec3 H = normalize(L + V);
  vec3 sun = F.sunColor.rgb * F.sunDir.w;

  // Local frame of the eyeball: +z looks forward, +y up.
  vec3 dir = normalize(vLocal);
  float gaze = dot(dir, vec3(0.0, 0.0, 1.0));      // 1 on the pupil axis
  float ang = acos(clamp(gaze, -1.0, 1.0));          // 0 at the pupil centre
  float az = atan(dir.y, dir.x);

  const float IRIS = 0.78;    // iris angular radius (rad)
  const float PUPIL = 0.235;

  // ---------------------------------------------------------------- sclera ---
  float veins = fbm(vec2(az * 3.0, ang * 9.0) + 4.0, 4) - 0.5;
  vec3 sclera = vec3(0.52, 0.46, 0.42) * (0.94 + 0.14 * veins);
  // Warmer, slightly bloodshot towards the corners.
  sclera = mix(sclera, vec3(0.60, 0.42, 0.38), smoothstep(0.9, 1.5, ang) * 0.55);
  // Ambient occlusion from the lids.
  sclera *= 0.42 + 0.58 * smoothstep(0.0, 1.5, ang);

  // ------------------------------------------------------------------ iris ---
  vec3 irisOuter = vec3(0.075, 0.038, 0.016);
  vec3 irisInner = vec3(0.230, 0.120, 0.048);
  float fibres = fbm(vec2(az * 9.0, ang * 4.5), 4);
  float irisT = smoothstep(IRIS, PUPIL, ang);
  vec3 iris = mix(irisOuter, irisInner, saturate(irisT * 1.15));
  iris *= 0.70 + 0.70 * fibres;
  // Limbal ring.
  iris = mix(iris, vec3(0.030, 0.016, 0.010), smoothstep(IRIS * 0.80, IRIS, ang));
  // Collarette highlight around the pupil.
  iris += vec3(0.10, 0.055, 0.020) * smoothstep(PUPIL * 2.4, PUPIL * 1.1, ang + 0.10 * fibres);

  vec3 albedo = mix(sclera, iris, smoothstep(IRIS + 0.02, IRIS - 0.03, ang));
  albedo = mix(albedo, vec3(0.008, 0.007, 0.007), smoothstep(PUPIL + 0.02, PUPIL - 0.01, ang));

  float sh = mix(0.45, 1.0, shadowFactor(vWorld, N, 0.0015));
  float ndl = max(dot(N, L), 0.0);
  vec3 color = albedo * (sun * ndl * sh * 0.90 + skyAmbient(N) * 0.45);

  // Cornea: a wet, very tight highlight plus a sky reflection.
  vec3 C = normalize(L + V);
  float spec = pow(max(dot(N, C), 0.0), 900.0) * 3.2;
  color += sun * spec * sh;
  float ndv = max(dot(N, V), 1e-4);
  float fres = 0.04 + 0.96 * pow(1.0 - ndv, 5.0);
  color += skyAmbient(reflect(-V, N)) * fres * 0.22;

  color = applyFog(color, vWorld);
  outColor = vec4(sanitizeHDR(color), 1.0);
}
