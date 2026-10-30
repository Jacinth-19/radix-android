// Camera-facing ribbon expansion shared by the hair colour and shadow passes.
#ifndef RIBBON_GLSL
#define RIBBON_GLSL

// Returns the widened world position of a ribbon vertex, and reports the
// view-facing ribbon normal through `outNormal`.
vec3 expandRibbon(vec3 pos, vec3 tangent, float sideSign, float u, float rnd,
                  float width, vec3 camPos, out vec3 outNormal, out vec3 outViewDir) {
  vec3 T = normalize(tangent);
  vec3 V = normalize(camPos - pos);
  vec3 side = cross(T, V);
  float sl = length(side);
  // Fallback for strands that point straight at the camera.
  side = (sl > 1e-5) ? side / sl : normalize(cross(T, vec3(0.0, 1.0, 0.0)) + vec3(1e-4, 0.0, 0.0));
  float taper = mix(1.0, 0.28, smoothstep(0.80, 1.0, u));  // soft, pointed tips
  float w = width * (0.95 + 0.35 * u) * (0.70 + 0.60 * rnd) * taper;
  vec3 p = pos + side * (sideSign * 0.5 * w);
  vec3 N = cross(side, T);
  float nl = length(N);
  N = (nl > 1e-6) ? N / nl : V;
  if (dot(N, V) < 0.0) N = -N;
  outNormal = N;
  outViewDir = V;
  return p;
}

#endif
