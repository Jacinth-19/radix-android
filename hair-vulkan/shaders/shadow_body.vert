#version 460
#extension GL_GOOGLE_include_directive : require
#include "uniforms.glsl"

layout(location = 0) in vec3 inPos;
layout(location = 1) in vec3 inNormal;

void main() {
  gl_Position = F.lightViewProj * vec4(inPos, 1.0);
}
