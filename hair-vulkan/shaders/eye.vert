#version 460
#extension GL_GOOGLE_include_directive : require
#include "uniforms.glsl"

layout(location = 0) in vec3 inPos;
layout(location = 1) in vec3 inNormal;
layout(location = 2) in vec2 inUV;

layout(location = 0) out vec3 vWorld;
layout(location = 1) out vec3 vNormal;
layout(location = 2) out vec3 vLocal;   // direction from the eyeball centre

void main() {
  // Pick the nearer eyeball centre (F.eye0 / F.eye1: xyz = centre, w = radius).
  float d0 = distance(inPos, F.eye0.xyz);
  float d1 = distance(inPos, F.eye1.xyz);
  vec3 centre = d0 <= d1 ? F.eye0.xyz : F.eye1.xyz;
  float radius = d0 <= d1 ? F.eye0.w : F.eye1.w;

  vWorld = inPos;
  vNormal = inNormal;
  vLocal = (inPos - centre) / max(radius, 1e-5);
  gl_Position = F.viewProj * vec4(inPos, 1.0);
}
