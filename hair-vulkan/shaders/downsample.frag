#version 460
#extension GL_GOOGLE_include_directive : require
#include "uniforms.glsl"

layout(set = 0, binding = 2) uniform sampler2D texA;

layout(location = 0) in vec2 vUV;
layout(location = 0) out vec4 outColor;

void main() {
  vec2 texel = 1.0 / vec2(textureSize(texA, 0));
  vec3 c = texture(texA, vUV + texel * vec2(-1.0, -1.0)).rgb;
  c += texture(texA, vUV + texel * vec2(1.0, -1.0)).rgb;
  c += texture(texA, vUV + texel * vec2(-1.0, 1.0)).rgb;
  c += texture(texA, vUV + texel * vec2(1.0, 1.0)).rgb;
  c *= 0.25;
  outColor = vec4(c, 1.0);
}
