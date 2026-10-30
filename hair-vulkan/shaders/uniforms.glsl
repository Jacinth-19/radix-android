// Shared frame uniform block (std140, mirrored by hv::FrameUniforms on the CPU).
#ifndef UNIFORMS_GLSL
#define UNIFORMS_GLSL

layout(set = 0, binding = 0) uniform Frame {
  mat4 viewProj;
  mat4 lightViewProj;
  mat4 invViewProj;
  vec4 camPos;       // xyz = camera position, w = time
  vec4 sunDir;       // xyz = direction towards the sun, w = sun intensity
  vec4 sunColor;     // rgb = sun colour, w = ambient scale
  vec4 params;       // x = hair ribbon width, y = exposure, z = bloom, w = fog density
  vec4 params2;      // x = hair self-shadow, y = wind, z = frame index, w = unused
  vec4 skyParams;    // x = sun disk power, y = cloud cover, z = sky gain, w = horizon haze
  vec4 shadowParams; // x = 1/shadowSize, y = depth bias, z = shadow size, w = unused
  vec4 hairColor;    // rgb = hair base colour, w = specular gain
} F;

const float PI = 3.14159265359;

float saturate(float x) { return clamp(x, 0.0, 1.0); }
vec3 saturate(vec3 x) { return clamp(x, vec3(0.0), vec3(1.0)); }

// Keeps the HDR buffer free of NaN/Inf: one poisoned fragment would otherwise be
// smeared into a large visible square by the bloom pyramid.
vec3 sanitizeHDR(vec3 c) {
  if (any(isnan(c)) || any(isinf(c))) return vec3(0.0);
  return clamp(c, vec3(0.0), vec3(60000.0));
}

// Image-space uv -> world-space ray direction (uv.y = 0 at the top of the image).
vec3 rayDirection(vec2 uv) {
  vec2 ndc = vec2(uv.x * 2.0 - 1.0, 1.0 - uv.y * 2.0);
  vec4 p = F.invViewProj * vec4(ndc, 1.0, 1.0);
  return normalize(p.xyz / p.w - F.camPos.xyz);
}

#endif
