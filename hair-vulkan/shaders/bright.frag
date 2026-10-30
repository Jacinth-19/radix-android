#version 460
#extension GL_GOOGLE_include_directive : require
#include "uniforms.glsl"

layout(set = 0, binding = 2) uniform sampler2D texA;

layout(location = 0) in vec2 vUV;
layout(location = 0) out vec4 outColor;

void main() {
  vec2 texel = 1.0 / vec2(textureSize(texA, 0));
  vec3 c = texture(texA, vUV + texel * vec2(-0.5, -0.5)).rgb;
  c += texture(texA, vUV + texel * vec2(0.5, -0.5)).rgb;
  c += texture(texA, vUV + texel * vec2(-0.5, 0.5)).rgb;
  c += texture(texA, vUV + texel * vec2(0.5, 0.5)).rgb;
  c *= 0.25;

  float luma = dot(c, vec3(0.2126, 0.7152, 0.0722));
  float knee = 1.0;
  float soft = clamp(luma - knee * 0.5, 0.0, 2.0 * knee);
  soft = soft * soft / (4.0 * knee + 1e-4);
  float contribution = max(soft, luma - knee) / max(luma, 1e-4);
  outColor = vec4(c * contribution, 1.0);
}
