#version 460
#extension GL_GOOGLE_include_directive : require
#include "uniforms.glsl"
#include "ribbon.glsl"

layout(location = 0) in vec3 inPos;
layout(location = 1) in vec3 inTangent;
layout(location = 2) in float inSide;
layout(location = 3) in float inU;
layout(location = 4) in float inRnd;

void main() {
  vec3 N;
  vec3 V;
  vec3 p = expandRibbon(inPos, inTangent, inSide, inU, inRnd, F.params.x, F.camPos.xyz, N, V);
  gl_Position = F.lightViewProj * vec4(p, 1.0);
}
