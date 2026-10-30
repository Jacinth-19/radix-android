// hair-vulkan — real-time hair-mesh simulation + Vulkan renderer.
//
// Head-less by design: renders into an off-screen HDR target, post-processes it
// (bloom, ACES tonemap, FXAA) and writes PNG frames. Runs on a GPU or on Mesa's
// lavapipe (llvmpipe) software Vulkan driver.
#include <sys/stat.h>
#include <sys/types.h>

#include <algorithm>
#include <chrono>
#include <cmath>
#include <cstdio>
#include <cstring>
#include <iostream>
#include <string>
#include <vector>

#include "hair.hpp"
#include "png_writer.hpp"
#include "scene.hpp"
#include "vk_engine.hpp"

using namespace hv;

namespace {

struct Shot {
  const char* name;
  float yaw, pitch, dist, targetY, fov;
};

const Shot kShots[] = {
    {"hero", 0.55f, 0.045f, 1.32f, 1.520f, 33.0f},
    {"front", 0.06f, 0.02f, 1.28f, 1.530f, 33.0f},
    {"threequarter", -0.70f, 0.04f, 1.30f, 1.525f, 33.0f},
    {"profile", 1.50f, 0.02f, 1.30f, 1.530f, 33.0f},
    {"back", 3.02f, 0.05f, 1.35f, 1.520f, 33.0f},
    {"detail", 0.85f, -0.04f, 0.58f, 1.560f, 30.0f},
    {"wide", 0.70f, 0.09f, 3.10f, 1.230f, 38.0f},
};
const int kShotCount = (int)(sizeof(kShots) / sizeof(kShots[0]));

struct Options {
  int width = 1280;
  int height = 720;
  int msaa = 4;
  int settleSteps = 80;
  int animFrames = 0;
  float wind = 0.45f;
  float exposure = 0.52f;
  float bloomStrength = 0.055f;
  bool bloom = true;
  bool fxaa = true;
  bool interactive = false;
  bool noBody = false;
  float shadowBias = 0.0022f;
  bool noHair = false;
  int debugView = 0;
  int bloomLevels = 5;
  bool listShots = false;
  bool shotsExplicit = false;
  std::string outDir = "out";
  std::string spvDir = "spv";
  std::vector<std::string> shotNames;
};

void printUsage() {
  std::printf(
      "hair-vulkan — hair-mesh simulation renderer (Vulkan 1.3, head-less)\n\n"
      "usage: hair_vulkan [options]\n"
      "  --width N --height N        output resolution (default 1280x720)\n"
      "  --msaa N                    MSAA samples: 1, 2 or 4 (default 4)\n"
      "  --shot NAME                 render one shot (repeatable); names: ");
  for (int i = 0; i < kShotCount; ++i) std::printf("%s%s", kShots[i].name, i + 1 < kShotCount ? ", " : "\n");
  std::printf(
      "  --all-shots                 render every predefined shot (default)\n"
      "  --frames N                  also render N animation frames of the wind\n"
      "  --wind F                    wind strength (default 1.0)\n"
      "  --exposure F                tonemap exposure (default 1.05)\n"
      "  --bloom F                   bloom strength (default 0.055)\n"
      "  --no-bloom / --no-fxaa      disable a post-processing stage\n"
      "  --settle N                  simulation warm-up steps (default 110)\n"
      "  --out DIR                   output directory for PNG frames\n"
      "  --spv DIR                   directory with the compiled SPIR-V shaders\n"
      "  --interactive               camera console: a/d orbit, w/s pitch, q/e dolly, p=save frame\n"
      "  --list-shots                print the shot list and exit\n");
}

bool parseArgs(int argc, char** argv, Options& opt) {
  for (int i = 1; i < argc; ++i) {
    std::string a = argv[i];
    auto next = [&](float& v) {
      if (i + 1 < argc) v = std::strtof(argv[++i], nullptr);
    };
    auto nextInt = [&](int& v) {
      if (i + 1 < argc) v = std::atoi(argv[++i]);
    };
    if (a == "--width") nextInt(opt.width);
    else if (a == "--height") nextInt(opt.height);
    else if (a == "--msaa") nextInt(opt.msaa);
    else if (a == "--settle") nextInt(opt.settleSteps);
    else if (a == "--frames") nextInt(opt.animFrames);
    else if (a == "--wind") next(opt.wind);
    else if (a == "--exposure") next(opt.exposure);
    else if (a == "--bloom") next(opt.bloomStrength);
    else if (a == "--no-bloom") opt.bloom = false;
    else if (a == "--no-fxaa") opt.fxaa = false;
    else if (a == "--out") { if (i + 1 < argc) opt.outDir = argv[++i]; }
    else if (a == "--spv") { if (i + 1 < argc) opt.spvDir = argv[++i]; }
    else if (a == "--shot") { if (i + 1 < argc) { opt.shotNames.push_back(argv[++i]); opt.shotsExplicit = true; } }
    else if (a == "--all-shots") { opt.shotNames.clear(); opt.shotsExplicit = true; }
    else if (a == "--interactive") opt.interactive = true;
    else if (a == "--no-body") opt.noBody = true;
    else if (a == "--shadow-bias") next(opt.shadowBias);
    else if (a == "--no-hair") opt.noHair = true;
    else if (a == "--debug-view") nextInt(opt.debugView);
    else if (a == "--bloom-levels") nextInt(opt.bloomLevels);
    else if (a == "--list-shots") opt.listShots = true;
    else if (a == "--help" || a == "-h") { printUsage(); return false; }
    else {
      std::fprintf(stderr, "unknown option: %s\n", a.c_str());
      printUsage();
      return false;
    }
  }
  return true;
}

FrameUniforms makeUniforms(const Camera& cam, float aspect, float time, const Options& opt,
                           float windStrength, int shadowSize, float frameIndex) {
  FrameUniforms u{};
  Mat4 view = cam.view();
  Mat4 proj = cam.proj(aspect);
  Mat4 vp = proj * view;
  Mat4 inv = inverse(vp);

  std::memcpy(u.viewProj, vp.m, sizeof(u.viewProj));
  std::memcpy(u.invViewProj, inv.m, sizeof(u.invViewProj));

  // Sun.
  Vec3 sunDir = normalize(Vec3(0.52f, 0.66f, 0.55f));
  Vec3 center(0.0f, 1.44f, 0.0f);
  float radius = 1.35f;
  Mat4 lightView = Mat4::lookAt(center + sunDir * 2.6f, center, Vec3(0, 1, 0));
  Mat4 lightProj = Mat4::ortho(-radius, radius, -radius, radius, 0.05f, 5.4f);
  Mat4 lightVP = lightProj * lightView;
  std::memcpy(u.lightViewProj, lightVP.m, sizeof(u.lightViewProj));

  Vec3 eye = cam.eye();
  u.camPos[0] = eye.x; u.camPos[1] = eye.y; u.camPos[2] = eye.z; u.camPos[3] = time;
  u.sunDir[0] = sunDir.x; u.sunDir[1] = sunDir.y; u.sunDir[2] = sunDir.z; u.sunDir[3] = 2.35f;
  u.sunColor[0] = 1.00f; u.sunColor[1] = 0.930f; u.sunColor[2] = 0.825f; u.sunColor[3] = 1.0f;

  u.params[0] = 0.0019f;          // hair ribbon width (m)
  u.params[1] = opt.exposure;     // exposure
  u.params[2] = opt.bloom ? opt.bloomStrength : 0.0f;
  u.params[3] = 0.0055f;          // fog density

  u.params2[0] = 1.0f;            // hair self shadow
  u.params2[1] = windStrength;
  u.params2[2] = frameIndex;
  u.params2[3] = 0.0f;

  u.skyParams[0] = 1.0f;   // sun disk power
  u.skyParams[1] = 0.20f;  // cloud cover
  u.skyParams[2] = 1.0f;   // sky gain
  u.skyParams[3] = 0.30f;  // horizon haze

  u.shadowParams[0] = 1.0f / (float)shadowSize;
  u.shadowParams[1] = opt.shadowBias;
  u.shadowParams[2] = (float)shadowSize;
  u.shadowParams[3] = 0.0f;

  u.hairColor[0] = 0.058f;  // dark warm brown, linear
  u.hairColor[1] = 0.032f;
  u.hairColor[2] = 0.017f;
  u.hairColor[3] = 0.55f;   // specular gain
  return u;
}

}  // namespace

int main(int argc, char** argv) {
  Options opt;
  if (!parseArgs(argc, argv, opt)) return opt.listShots ? 0 : 1;
  if (opt.listShots) {
    for (int i = 0; i < kShotCount; ++i) std::printf("%s\n", kShots[i].name);
    return 0;
  }
  if (opt.shotNames.empty()) {
    for (int i = 0; i < kShotCount; ++i) opt.shotNames.push_back(kShots[i].name);
  }

  // Make sure the output directory exists.
  for (size_t i = 1; i <= opt.outDir.size(); ++i) {
    if (i == opt.outDir.size() || opt.outDir[i] == '/') {
      std::string sub = opt.outDir.substr(0, i);
      if (!sub.empty()) ::mkdir(sub.c_str(), 0775);
    }
  }

  std::printf("hair-vulkan: real-time hair mesh simulation (Wu & Yuksel, I3D 2016)\n");

  // ------------------------------------------------------------------ scene --
  BodyScene scene = buildBodyScene();

  HairStyle style;
  style.cols = 212;
  style.rows = 48;
  style.nodes = 20;
  style.length = 0.520f;
  style.ribbonWidth = 0.0019f;
  style.waveAmp = 0.010f;
  style.waveFreq = 2.0f;
  style.lengthJitter = 0.05f;

  HairMesh hair;
  hair.build(scene.shape, style);
  std::printf("hair mesh: %zu strands x %d nodes (%zu ribbon triangles)\n", hair.strandCount(),
              hair.nodes(), hair.triangleCount());

  WindParams wind;
  wind.strength = opt.wind;
  wind.time = 0.0f;

  // Warm-up: let the hair settle on the shoulders before the first frame.
  auto simStart = std::chrono::high_resolution_clock::now();
  hair.settle(opt.settleSteps, 1.0f / 60.0f, wind);
  auto simEnd = std::chrono::high_resolution_clock::now();
  double settleMs = std::chrono::duration<double, std::milli>(simEnd - simStart).count();
  std::printf("simulation warm-up: %d steps in %.1f ms (%.2f ms/step)\n", opt.settleSteps, settleMs,
              settleMs / std::max(1, opt.settleSteps));
  wind.time = (float)opt.settleSteps / 60.0f;

  // Ribbon vertices + index buffer.
  std::vector<HairVertex> verts;
  hair.writeRibbonVertices(verts);
  std::vector<uint32_t> indices;
  indices.reserve(hair.indexCount());
  const int nodes = hair.nodes();
  const size_t strands = hair.strandCount();
  for (size_t s = 0; s < strands; ++s) {
    const uint32_t base = (uint32_t)(s * (size_t)nodes * 2u);
    for (int i = 0; i + 1 < nodes; ++i) {
      const uint32_t a = base + (uint32_t)i * 2u;
      const uint32_t b = a + 1u;
      const uint32_t c = a + 2u;
      const uint32_t d = a + 3u;
      indices.push_back(a); indices.push_back(b); indices.push_back(c);
      indices.push_back(b); indices.push_back(d); indices.push_back(c);
    }
  }

  // ---------------------------------------------------------------- renderer --
  RenderConfig rcfg;
  rcfg.width = opt.width;
  rcfg.height = opt.height;
  rcfg.msaa = opt.msaa;
  rcfg.bloom = opt.bloom;
  rcfg.fxaa = opt.fxaa;
  rcfg.bloomLevels = opt.bloomLevels;
  rcfg.spirvDir = opt.spvDir;

  Renderer renderer;
  std::string error;
  if (!renderer.init(rcfg, &error)) {
    std::fprintf(stderr, "renderer init failed: %s\n", error.c_str());
    return 1;
  }
  std::printf("device: %s (MSAA %dx, bloom %s, FXAA %s)\n", renderer.deviceName().c_str(),
              renderer.config().msaa, opt.bloom ? "on" : "off", opt.fxaa ? "on" : "off");
  renderer.drawBody = !opt.noBody;
  renderer.drawHair = !opt.noHair;
  renderer.debugView = (float)opt.debugView;
  renderer.setBodyMesh(scene.mesh);
  renderer.setHairGeometry(verts, indices);

  const float aspect = (float)opt.width / (float)opt.height;
  const int shadowSize = renderer.config().shadowSize;

  auto renderShot = [&](const Shot& shot, const std::string& filename, float frameIndex) {
    Camera cam;
    cam.yaw = shot.yaw;
    cam.pitch = shot.pitch;
    cam.dist = shot.dist;
    cam.target = Vec3(0.0f, shot.targetY, 0.0f);
    cam.fovDeg = shot.fov;

    hair.writeRibbonVertices(verts);
    renderer.updateHairVertices(verts);

    FrameUniforms u = makeUniforms(cam, aspect, wind.time, opt, wind.strength, shadowSize, frameIndex);
    std::string err;
    if (!renderer.render(u, &err)) {
      std::fprintf(stderr, "render failed: %s\n", err.c_str());
      return false;
    }
    std::string path = opt.outDir + "/" + filename;
    if (!writePNG(path, opt.width, opt.height, renderer.readback().data(), false)) {
      std::fprintf(stderr, "failed to write %s\n", path.c_str());
      return false;
    }
    std::printf("  %-26s %6.1f ms\n", filename.c_str(), renderer.lastRenderMs());
    return true;
  };

  // ------------------------------------------------------------------ shots --
  int failures = 0;
  // In the camera console the shot list is skipped unless it was asked for.
  const std::vector<std::string> shots =
      (opt.interactive && !opt.shotsExplicit) ? std::vector<std::string>() : opt.shotNames;
  for (const std::string& name : shots) {
    const Shot* shot = nullptr;
    for (int i = 0; i < kShotCount; ++i) {
      if (name == kShots[i].name) shot = &kShots[i];
    }
    if (!shot) {
      std::fprintf(stderr, "unknown shot '%s'\n", name.c_str());
      failures++;
      continue;
    }
    if (!renderShot(*shot, std::string("hair_") + shot->name + ".png", 0.0f)) failures++;
  }

  // -------------------------------------------------------------- animation --
  if (opt.animFrames > 0) {
    Camera cam;
    cam.yaw = kShots[0].yaw;
    cam.pitch = kShots[0].pitch;
    cam.dist = kShots[0].dist;
    cam.target = Vec3(0.0f, kShots[0].targetY, 0.0f);
    cam.fovDeg = kShots[0].fov;

    for (int f = 0; f < opt.animFrames; ++f) {
      // Slow orbital camera + gusting wind, one simulated frame per image.
      cam.orbit(0.012f, 0.0012f * std::sin((float)f * 0.3f));
      hair.step(1.0f / 60.0f, wind, 6, 2);
      wind.time += 1.0f / 60.0f;
      char name[64];
      std::snprintf(name, sizeof(name), "wind_%03d.png", f);
      if (!renderShot(kShots[0], name, (float)f)) failures++;
    }
  }

  // ------------------------------------------------------------ interactive --
  if (opt.interactive) {
    Camera cam;
    cam.yaw = kShots[0].yaw;
    cam.pitch = kShots[0].pitch;
    cam.dist = kShots[0].dist;
    cam.target = Vec3(0.0f, kShots[0].targetY, 0.0f);
    std::printf(
        "interactive camera: a/d = orbit, w/s = pitch, q/e = dolly, z/x = wind, p = save frame, "
        "blank line = quit\n");
    int counter = 0;
    std::string line;
    while (true) {
      std::printf("> ");
      std::fflush(stdout);
      if (!std::getline(std::cin, line)) break;
      if (line.empty()) break;
      for (char c : line) {
        switch (c) {
          case 'a': cam.orbit(-0.10f, 0.0f); break;
          case 'd': cam.orbit(0.10f, 0.0f); break;
          case 'w': cam.orbit(0.0f, 0.06f); break;
          case 's': cam.orbit(0.0f, -0.06f); break;
          case 'q': cam.dolly(0.12f); break;
          case 'e': cam.dolly(-0.12f); break;
          case 'z': wind.strength = std::max(0.0f, wind.strength - 0.2f); break;
          case 'x': wind.strength = std::min(3.0f, wind.strength + 0.2f); break;
          default: break;
        }
      }
      hair.step(1.0f / 60.0f, wind, 6, 2);
      wind.time += 1.0f / 60.0f;
      hair.writeRibbonVertices(verts);
      renderer.updateHairVertices(verts);
      FrameUniforms u = makeUniforms(cam, aspect, wind.time, opt, wind.strength, shadowSize, 0.0f);
      std::string err;
      if (!renderer.render(u, &err)) {
        std::fprintf(stderr, "render failed: %s\n", err.c_str());
        break;
      }
      char name[64];
      std::snprintf(name, sizeof(name), "interactive_%03d.png", counter++);
      writePNG(opt.outDir + "/" + name, opt.width, opt.height, renderer.readback().data(), false);
      std::printf("yaw=%.2f pitch=%.2f dist=%.2f wind=%.2f -> %s (%.1f ms)\n", cam.yaw, cam.pitch,
                  cam.dist, wind.strength, name, renderer.lastRenderMs());
    }
  }

  renderer.destroy();
  if (failures) {
    std::fprintf(stderr, "%d frame(s) failed\n", failures);
    return 1;
  }
  std::printf("done\n");
  return 0;
}
