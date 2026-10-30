#version 460

layout(location = 0) out vec2 vUV;

// Full-screen triangle; no vertex buffer needed.
void main() {
  vec2 p = vec2(float((gl_VertexIndex << 1) & 2), float(gl_VertexIndex & 2));
  vUV = vec2(p.x, 1.0 - p.y);  // (0,0) at the top-left of the image
  gl_Position = vec4(p * 2.0 - 1.0, 0.0, 1.0);
}
