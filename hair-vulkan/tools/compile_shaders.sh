#!/usr/bin/env bash
# Compiles every GLSL stage to SPIR-V with glslangValidator.
set -euo pipefail
here="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
GLSLANG="${GLSLANG:-$(command -v glslangValidator || command -v glslang || true)}"
if [[ -z "${GLSLANG}" ]]; then
  echo "glslangValidator not found; run tools/setup_vulkan_sdk.sh" >&2
  exit 1
fi
out="${1:-${here}/build/spv}"
mkdir -p "${out}"

# Explicit list keeps the SPIR-V names identical to the names used in C++.
"${GLSLANG}" -V --target-env vulkan1.3 -S vert -I"${here}/shaders" -o "${out}/fullscreen.spv" "${here}/shaders/fullscreen.vert"
"${GLSLANG}" -V --target-env vulkan1.3 -S frag -I"${here}/shaders" -o "${out}/sky.spv"        "${here}/shaders/sky.frag"
"${GLSLANG}" -V --target-env vulkan1.3 -S frag -I"${here}/shaders" -o "${out}/ground.spv"     "${here}/shaders/ground.frag"
"${GLSLANG}" -V --target-env vulkan1.3 -S vert -I"${here}/shaders" -o "${out}/body.spv"       "${here}/shaders/body.vert"
"${GLSLANG}" -V --target-env vulkan1.3 -S frag -I"${here}/shaders" -o "${out}/body_frag.spv"  "${here}/shaders/body.frag"
"${GLSLANG}" -V --target-env vulkan1.3 -S vert -I"${here}/shaders" -o "${out}/hair.spv"       "${here}/shaders/hair.vert"
"${GLSLANG}" -V --target-env vulkan1.3 -S frag -I"${here}/shaders" -o "${out}/hair_frag.spv"  "${here}/shaders/hair.frag"
"${GLSLANG}" -V --target-env vulkan1.3 -S vert -I"${here}/shaders" -o "${out}/shadow_body.spv" "${here}/shaders/shadow_body.vert"
"${GLSLANG}" -V --target-env vulkan1.3 -S vert -I"${here}/shaders" -o "${out}/shadow_hair.spv" "${here}/shaders/shadow_hair.vert"
"${GLSLANG}" -V --target-env vulkan1.3 -S frag -I"${here}/shaders" -o "${out}/bright.spv"     "${here}/shaders/bright.frag"
"${GLSLANG}" -V --target-env vulkan1.3 -S frag -I"${here}/shaders" -o "${out}/downsample.spv" "${here}/shaders/downsample.frag"
"${GLSLANG}" -V --target-env vulkan1.3 -S frag -I"${here}/shaders" -o "${out}/composite.spv"  "${here}/shaders/composite.frag"
"${GLSLANG}" -V --target-env vulkan1.3 -S frag -I"${here}/shaders" -o "${out}/fxaa.spv"       "${here}/shaders/fxaa.frag"

echo "SPIR-V written to ${out}"
ls -1 "${out}" | sed 's/^/  /'
