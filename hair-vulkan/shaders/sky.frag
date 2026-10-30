#version 460
#extension GL_GOOGLE_include_directive : require
#include "lighting.glsl"

layout(location = 0) in vec2 vUV;
layout(location = 0) out vec4 outColor;

void main() {
  vec3 dir = rayDirection(vUV);
  outColor = vec4(sanitizeHDR(skyRadiance(dir)), 1.0);
}
