#version 460
#extension GL_GOOGLE_include_directive : require
#include "uniforms.glsl"
#include "ribbon.glsl"

layout(location = 0) in vec3 inPos;
layout(location = 1) in vec3 inTangent;
layout(location = 2) in float inSide;
layout(location = 3) in float inU;
layout(location = 4) in float inRnd;

layout(location = 0) out vec3 vWorld;
layout(location = 1) out vec3 vTangent;
layout(location = 2) out vec3 vNormal;
layout(location = 3) out float vU;
layout(location = 4) out float vRnd;
layout(location = 5) out vec3 vSide;
layout(location = 6) out float vSideSign;

void main() {
  vec3 N;
  vec3 V;
  vec3 p = expandRibbon(inPos, inTangent, inSide, inU, inRnd, F.params.x, F.camPos.xyz, N, V);
  vWorld = p;
  vTangent = normalize(inTangent);
  vNormal = N;
  vU = inU;
  vRnd = inRnd;
  vSide = normalize(cross(normalize(inTangent), normalize(F.camPos.xyz - p)) + vec3(1e-6));
  vSideSign = inSide;
  gl_Position = F.viewProj * vec4(p, 1.0);
}
