#version 460
#extension GL_GOOGLE_include_directive : require
#include "lighting.glsl"

layout(location = 0) in vec3 vWorld;
layout(location = 1) in vec3 vNormal;
layout(location = 2) in vec2 vUV;

layout(location = 0) out vec4 outColor;

// Face landmark coordinates in world space. They match the sculpt produced by
// tools/build_head_asset.py (crown at y = 1.66, eyes at y = 1.5556, +z forward).
const vec3 EYE_L = vec3(-0.0315, 0.0, 0.052);
const vec3 EYE_R = vec3(0.0315, 0.0, 0.052);
const float FACE_Y = 1.5556;
const vec3 LIP_C = vec3(0.0, 1.4994, 0.0740);
const vec3 SKIN = vec3(0.360, 0.190, 0.140);

float ellipse(vec3 p, vec3 c, vec2 r) {
  vec2 d = (p.xy - c.xy) / r;
  return dot(d, d);
}

void main() {
  vec3 N = normalize(vNormal);
  vec3 V = normalize(F.camPos.xyz - vWorld);
  vec3 L = normalize(F.sunDir.xyz);
  vec3 H = normalize(L + V);
  vec3 sun = F.sunColor.rgb * F.sunDir.w;
  // Warm the sky ambient slightly so shadowed skin does not turn purple.
  vec3 amb = mix(skyAmbient(N), vec3(0.34, 0.30, 0.26), 0.42);

  // ---------------------------------------------------------------- skin -----
  float grain = fbm(vWorld.xz * 260.0 + vWorld.y * 210.0, 3) - 0.5;
  float blotch = fbm(vWorld.xy * 46.0 + vWorld.z * 31.0, 4) - 0.5;
  vec3 albedo = SKIN * (0.94 + 0.10 * blotch + 0.05 * grain);
  // Warm gradient: cooler/darker towards the jaw and the hairline.
  albedo *= mix(vec3(0.86, 0.80, 0.80), vec3(1.02, 1.0, 0.98),
                smoothstep(1.44, 1.58, vWorld.y));

  float face = smoothstep(1.395, 1.455, vWorld.y) * smoothstep(-0.020, 0.010, vWorld.z);
  float front = smoothstep(0.010, 0.045, vWorld.z) * face;

  // Eyes: socket shading, brows, lashes and a soft lid crease.
  float dL = ellipse(vWorld, vec3(EYE_L.x, FACE_Y, 0.0), vec2(0.030, 0.019));
  float dR = ellipse(vWorld, vec3(EYE_R.x, FACE_Y, 0.0), vec2(0.030, 0.019));
  float socket = exp(-2.2 * min(dL, dR)) * front;

  // Brows: arched, thick at the inner end, tapering outwards, sitting above the lid.
  float bx = vWorld.x;
  float browY = 1.5735 + 0.0042 * exp(-pow((abs(bx) - 0.0350) / 0.015, 2.0));
  float browBand = exp(-pow((vWorld.y - browY) / 0.0032, 2.0));
  float browSpanL = (1.0 - smoothstep(0.0430, 0.0555, -bx)) * smoothstep(0.0070, 0.0160, -bx);
  float browSpanR = (1.0 - smoothstep(0.0430, 0.0555, bx)) * smoothstep(0.0070, 0.0160, bx);
  float brow = browBand * saturate(browSpanL + browSpanR) * front;

  // Lash line: a thin dark band hugging the upper rim of the eye opening.
  float rimL = exp(-pow((sqrt(max(dL, 0.0)) - 0.94) / 0.10, 2.0)) * smoothstep(-0.002, 0.006, vWorld.y - FACE_Y);
  float rimR = exp(-pow((sqrt(max(dR, 0.0)) - 0.94) / 0.10, 2.0)) * smoothstep(-0.002, 0.006, vWorld.y - FACE_Y);
  float lashes = saturate(rimL + rimR) * front;

  // Lips: a soft cupid's bow shape with a lighter centre.
  vec2 lipD = (vWorld.xy - LIP_C.xy);
  float lipW = 0.0180 * (1.0 - 0.20 * smoothstep(0.0, 0.009, abs(lipD.y)));
  float lipMask = exp(-pow(lipD.x / lipW, 2.0) * 1.6) *
                  exp(-pow((lipD.y + 0.0012 * cos(lipD.x * 190.0)) / 0.0074, 2.0));
  lipMask *= smoothstep(0.052, 0.064, vWorld.z) * face;

  // Cheeks and nose: gentle warmth instead of hard masks.
  float blushL = exp(-length(vec2(vWorld.x + 0.048, vWorld.y - 1.523)) * 46.0);
  float blushR = exp(-length(vec2(vWorld.x - 0.048, vWorld.y - 1.523)) * 46.0);
  float blush = saturate(blushL + blushR) * front;
  float noseTip = exp(-length(vec2(vWorld.x, vWorld.y - 1.5455)) * 70.0) * front;

  vec3 lipCol = vec3(0.200, 0.052, 0.048);
  albedo = mix(albedo, albedo * vec3(0.90, 0.80, 0.80), socket * 0.70);
  albedo = mix(albedo, vec3(0.048, 0.028, 0.020), brow * 0.90);
  albedo = mix(albedo, vec3(0.022, 0.016, 0.014), lashes * 0.92);
  albedo = mix(albedo, lipCol, saturate(lipMask) * 0.95);
  albedo = mix(albedo, albedo * vec3(1.06, 0.84, 0.82), blush * 0.55);
  albedo = mix(albedo, albedo * vec3(1.03, 0.93, 0.91), noseTip * 0.35);

  // ------------------------------------------------------------- shading -----
  float sh = mix(0.35, 1.0, shadowFactor(vWorld, N, 0.0020));
  float ndl = dot(N, L);
  float wrapped = saturate((ndl + 0.45) / 1.45);

  // Subsurface: warm light bleeding through the terminator and thin rims.
  float thin = pow(1.0 - saturate(dot(N, V)), 2.0);
  vec3 sss = vec3(0.075, 0.020, 0.010) * (pow(wrapped, 3.0) + 0.8 * thin);

  float ao = 0.80 + 0.20 * saturate(N.y * 0.5 + 0.5);
  ao *= mix(0.70, 1.0, sh);

  vec3 color = albedo * (sun * wrapped * sh * 1.05 + amb * ao * 1.35) + sss * sh * sun;

  // Specular: broad oily skin response; lips get a tighter, stronger highlight.
  float rough = mix(0.46, 0.20, saturate(lipMask));
  float a2 = rough * rough * rough * rough;
  float ndh = max(dot(N, H), 0.0);
  float dterm = ndh * ndh * (a2 - 1.0) + 1.0;
  float ggx = a2 / (PI * dterm * dterm);
  float ndv = max(dot(N, V), 1e-4);
  float fres = 0.028 + 0.972 * pow(1.0 - ndv, 5.0);
  float vis = 0.5 / max(ndv, 0.22);
  float specGain = mix(0.35, 1.15, saturate(lipMask));
  color += sun * sh * max(ndl, 0.0) * ggx * fres * vis * specGain;
  color += skyAmbient(reflect(-V, N)) * fres * 0.12;

  color = applyFog(color, vWorld);
  outColor = vec4(sanitizeHDR(color), 1.0);
}
