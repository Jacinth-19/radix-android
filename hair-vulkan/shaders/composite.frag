#version 460
#extension GL_GOOGLE_include_directive : require
#include "uniforms.glsl"

layout(set = 0, binding = 2) uniform sampler2D texA;  // HDR scene
layout(set = 0, binding = 3) uniform sampler2D texB;  // bloom level 0
layout(set = 0, binding = 4) uniform sampler2D texC;  // bloom level 1
layout(set = 0, binding = 5) uniform sampler2D texD;  // bloom level 2

layout(location = 0) in vec2 vUV;
layout(location = 0) out vec4 outColor;

layout(push_constant) uniform Push {
  vec4 p;  // x = debug view: 0 normal, 1 = bloom only, 2 = raw HDR, 3 = luma
} pc;

vec3 acesFilm(vec3 x) {
  const float a = 2.51;
  const float b = 0.03;
  const float c = 2.43;
  const float d = 0.59;
  const float e = 0.14;
  return clamp((x * (a * x + b)) / (x * (c * x + d) + e), 0.0, 1.0);
}

void main() {
  vec3 hdr = texture(texA, vUV).rgb;
  // Weighted blend of the bloom pyramid: wide, soft glow with no up-sampling pass.
  vec3 bloom = texture(texB, vUV).rgb * 0.50 + texture(texC, vUV).rgb * 0.32 +
               texture(texD, vUV).rgb * 0.18;

  if (pc.p.x > 0.5) {
    vec3 dbg;
    if (pc.p.x < 1.5) dbg = bloom * 40.0;
    else if (pc.p.x < 2.5) dbg = hdr / max(1.0, max(hdr.r, max(hdr.g, hdr.b)));
    else if (pc.p.x < 3.5) dbg = vec3(dot(hdr + bloom, vec3(0.2126, 0.7152, 0.0722)));
    else if (pc.p.x < 4.5) {  // NaN / Inf mask
      bool bad = any(isnan(hdr)) || any(isinf(hdr)) || any(isnan(bloom)) || any(isinf(bloom));
      outColor = vec4(bad ? vec3(1.0, 0.0, 0.0) : hdr / max(1.0, max(hdr.r, max(hdr.g, hdr.b))),
                      1.0);
      return;
    } else if (pc.p.x < 5.5) {  // per-source NaN/Inf mask: R=hdr G=L0 B=L1
      vec3 b0 = texture(texB, vUV).rgb;
      vec3 b1 = texture(texC, vUV).rgb;
      float bh = (any(isnan(hdr)) || any(isinf(hdr))) ? 1.0 : 0.0;
      float f0 = (any(isnan(b0)) || any(isinf(b0))) ? 1.0 : 0.0;
      float f1 = (any(isnan(b1)) || any(isinf(b1))) ? 1.0 : 0.0;
      outColor = vec4(bh, f0, f1, 1.0);
      return;
    } else if (pc.p.x < 6.5) {  // bloom level 2 magnitude
      vec3 b2 = texture(texD, vUV).rgb;
      outColor = vec4(b2 * 40.0, 1.0);
      return;
    } else {
      dbg = hdr;
    }
    outColor = vec4(pow(max(dbg, vec3(0.0)), vec3(1.0 / 2.2)), 1.0);
    return;
  }

  vec3 color = hdr + bloom * F.params.z;
  color *= F.params.y;                  // exposure

  color = acesFilm(color);              // filmic tonemap

  vec2 q = vUV - 0.5;
  float vignette = 1.0 - 0.28 * dot(q, q);
  color *= vignette;

  color = pow(max(color, vec3(0.0)), vec3(1.0 / 2.2));
  outColor = vec4(color, 1.0);
}
