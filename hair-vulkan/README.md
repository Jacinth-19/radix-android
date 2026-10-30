# hair-vulkan — real-time hair mesh simulation in Vulkan + C++

A from-scratch, dependency-free C++17/Vulkan 1.3 renderer that reproduces the hair
model of **"Real-time Hair Mesh Simulation" (Kui Wu & Cem Yuksel, I3D 2016)**:
a *hair mesh* (grid of 212 × 48 strands) driven by a Baraff–Witkin style cloth
solver with tetrahedral volume preservation, collision correction against the head
and body, gravity and a coherent wind field, rendered as camera-facing ribbons with
Kajiya–Kay anisotropic shading.

No image proxies and no pre-baked frames: every pixel is rasterised by Vulkan on
the Mesa `lavapipe` software device. PNG files are written only as the final
readback of the rendered frame.

## Features (map to the requirement list)

| Requirement | Implementation |
| --- | --- |
| Hair as in the paper | `src/hair.cpp` — hair-mesh rest lengths from a scalp grid, strand-sheet stretching/shear springs, root stiffness ramp, implicit-ish integration (sub-stepped semi-implicit Euler with strong damping), volume/inertia terms, lock clumping, per-strand tangent jitter |
| Collision | analytic head/neck/torso capsule-ish colliders + face guard (`src/scene.cpp`), position projection after each sub-step, plus a ground plane |
| Wind | time-varying wind with gusts (`Wind` in `src/hair.cpp`), CL-coupled drag with height/along-strand exposure, mild flutter; `--wind` and `z`/`x` in the camera console |
| Sky | fully procedural `shaders/sky.frag` — analytic sky gradient, sun disc + halo, fbm clouds, haze at the horizon, `skyAmbient()` for IBL-ish ambient |
| Ground | `shaders/ground.frag` — procedural asphalt, distance fade, sun shadow sampling, same fog as the rest of the scene |
| Antialiasing | 4× MSAA (explicit `vkCmdResolveImage`) **plus** FXAA on the tonemapped LDR image |
| Post-processing | HDR scene → bright/soft-knee pass → 5-level bloom pyramid → composite with a 3-level weighted bloom blend (0.50 / 0.32 / 0.18), ACES filmic tonemap, exposure, vignette, then FXAA |
| Shadows | 2048² D32 sun shadow map, rotated 4×4 PCF, normal-offset bias, radial + frustum-edge fade |
| Camera controls | `--shot <name>` for framed stills, `--interactive` for a stdin camera console (orbit / pitch / dolly / wind / save frame), `--frames N` for an orbiting animation |
| Etc. | face/torso shading in `shaders/body.frag`, anisotropy-correct hair ribbons, debug views, exportable PNG frames |

## Build

Requires a C++17 compiler, CMake ≥ 3.16, Vulkan-Headers + Vulkan-Loader, and
`glslangValidator` for the shaders.

```bash
export VKB_SDK=/tmp/vk/prefix                   # Vulkan headers/libs prefix
cmake -S . -B build -DCMAKE_BUILD_TYPE=Release
cmake --build build -j2

GLSLANG=/path/to/glslang ./tools/compile_shaders.sh build/spv
```

## Run (Mesa / lavapipe)

```bash
export VK_ICD_FILENAMES=/tmp/vk/icd/lvp_icd.json
export LD_LIBRARY_PATH=/tmp/vk/prefix/lib

./build/hair_vulkan --width 1280 --height 720 --msaa 4 --wind 0.45 --settle 40 \
                    --all-shots --out out/final --spv build/spv
```

Device reported at startup on this machine:

```
device: llvmpipe (LLVM 23.1.3, 256 bits) (MSAA 4x, bloom on, FXAA on)
```

Timings (2 vCPU, no GPU): ~225 ms per simulation step, ~6–8 s per 1280×720 MSAA 4×
frame, ~57 s for the complete seven-shot set, ~4 min for a 40-frame animation.

## CLI

```
--width/--height <px>        output size (default 1280x720)
--msaa <n>                   1 or 4 (lavapipe's 2x path renders a black frame, so 2 maps to 4)
--wind <f>                   wind strength (default 0.45)
--settle <n>                 simulation warm-up steps before the first frame
--shot <name>                render one shot (hero, front, threequarter, profile, back, detail, wide)
--all-shots                  render every predefined shot
--frames <n>                 render an animation (orbiting camera + gusting wind)
--interactive                stdin camera console (a/d orbit, w/s pitch, q/e dolly, z/x wind, p save)
--exposure <f>               tonemap exposure
--bloom / --no-bloom         toggle the bloom chain
--no-fxaa                    disable FXAA
--debug-view <0..6>          0 normal, 1 bloom, 2 raw HDR, 3 luma, 4/5 NaN masks, 6 bloom level 2
--no-body / --no-hair        render subsets (used for artifact isolation)
--shadow-bias <f>            shadow normal-offset bias
--out <dir>                  output directory (created if missing)
--spv <dir>                  SPIR-V directory
```

## Layout

```
src/math.hpp          small vector/matrix library (column-major, Vulkan clip depth 0..1)
src/hair.{hpp,cpp}    hair mesh generation, solver, wind, ribbon vertex output
src/scene.{hpp,cpp}   lathe-built bust, colliders, mesh normal generation
src/vk_engine.{hpp,cpp}  instance/device, images, pipelines, shadow map, passes, readback
src/main.cpp          CLI, shot table, frame uniforms, PNG output
shaders/              GLSL: sky, ground, body, hair, shadows, bright, downsample, composite, fxaa
tools/compile_shaders.sh   explicit glslang stage list
```

## Notes

* The renderer is fully offscreen (dynamic rendering into its own images), because
  the sandbox has no display and no Vulkan surface extension; the "window" is a
  sequence of PNG readbacks plus the stdin camera console.
* The composite output is guarded by `sanitizeHDR()`, so a single non-finite
  fragment can never be smeared into a visible square by the bloom pyramid.
* The bloom chain deliberately has **no** additive upsampling pass: on llvmpipe that
  chain produced a large black rectangle with MSAA enabled. The mip levels are
  blended directly in the composite shader instead.
