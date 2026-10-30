// hair-vulkan: minimal but complete Vulkan 1.3 renderer.
//
// Pipeline:
//   shadow pass (sun)  ->  4x MSAA HDR scene pass (sky / ground / bust / hair)
//   -> resolve  ->  bloom (bright + 4-level down/up chain)  ->  ACES composite
//   ->  FXAA  ->  RGBA8 image that is read back to PNG.
//
// Everything is rendered head-less: no swapchain, no window system, so the same
// binary runs on a GPU or on Mesa's lavapipe (llvmpipe) software Vulkan driver.
#pragma once

#include <cstdint>
#include <string>
#include <vector>

#include "hair.hpp"
#include "math.hpp"
#include "scene.hpp"

#include <vulkan/vulkan.h>

namespace hv {

// Mirrors `uniform Frame` in shaders/common.glsl (std140).
struct FrameUniforms {
  float viewProj[16];
  float lightViewProj[16];
  float invViewProj[16];
  float camPos[4];        // xyz = camera position, w = time (s)
  float sunDir[4];        // xyz = direction towards the sun, w = sun intensity
  float sunColor[4];      // rgb = sun colour, w = ambient scale
  float params[4];        // x = ribbon width, y = exposure, z = bloom strength, w = fog density
  float params2[4];       // x = hair self-shadow, y = wind strength, z = frame index, w = unused
  float skyParams[4];     // x = sun disk power, y = cloud cover, z = sky gain, w = horizon haze
  float shadowParams[4];  // x = 1/shadowSize, y = depth bias, z = shadow size, w = unused
  float hairColor[4];     // rgb = hair base colour, w = specular gain
  float eye0[4];          // xyz = left eyeball centre, w = radius
  float eye1[4];          // xyz = right eyeball centre, w = radius
};

struct RenderConfig {
  int width = 1280;
  int height = 720;
  int msaa = 4;
  bool bloom = true;
  bool fxaa = true;
  int bloomLevels = 5;
  int shadowSize = 2048;
  std::string spirvDir = "spv";
};

class Renderer {
 public:
  bool init(const RenderConfig& cfg, std::string* error);
  void destroy();

  void setBodyMesh(const Mesh& mesh);
  void setEyeMesh(const Mesh& mesh);
  void setHairGeometry(const std::vector<HairVertex>& verts, const std::vector<uint32_t>& indices);
  void updateHairVertices(const std::vector<HairVertex>& verts);
  bool render(const FrameUniforms& uniforms, std::string* error);

  const std::vector<uint8_t>& readback() const { return readbackPixels_; }
  bool drawBody = true;  // debug toggles
  bool drawEyes = true;
  bool drawHair = true;
  float debugView = 0.0f;  // 0 normal, 1 bloom, 2 raw HDR, 3 luma
  const RenderConfig& config() const { return cfg_; }
  const std::string& deviceName() const { return deviceName_; }
  uint32_t maxSampleCount() const { return maxSamples_; }
  double lastRenderMs() const { return lastRenderMs_; }

 private:
  struct Image {
    VkImage image = VK_NULL_HANDLE;
    VkDeviceMemory memory = VK_NULL_HANDLE;
    VkImageView view = VK_NULL_HANDLE;
    VkFormat format = VK_FORMAT_UNDEFINED;
    VkImageLayout layout = VK_IMAGE_LAYOUT_UNDEFINED;
    uint32_t width = 0, height = 0;
    VkImageAspectFlags aspect = VK_IMAGE_ASPECT_COLOR_BIT;
  };
  struct Buffer {
    VkBuffer buffer = VK_NULL_HANDLE;
    VkDeviceMemory memory = VK_NULL_HANDLE;
    VkDeviceSize size = 0;
    void* mapped = nullptr;
  };
  struct Pipeline {
    VkPipeline pipeline = VK_NULL_HANDLE;
    VkPipelineLayout layout = VK_NULL_HANDLE;
  };

  bool createInstance(std::string* error);
  bool pickDevice(std::string* error);
  bool createDevice(std::string* error);
  bool createResources(std::string* error);
  bool createPipelines(std::string* error);
  bool createDescriptors(std::string* error);

  uint32_t findMemoryType(uint32_t typeFilter, VkMemoryPropertyFlags props) const;
  bool createImage(Image& img, uint32_t w, uint32_t h, VkFormat format, VkSampleCountFlagBits samples,
                   VkImageUsageFlags usage, VkImageAspectFlags aspect, std::string* error);
  bool createBuffer(Buffer& buf, VkDeviceSize size, VkBufferUsageFlags usage,
                    VkMemoryPropertyFlags props, const void* data, std::string* error);
  void destroyBuffer(Buffer& buf);

  VkShaderModule loadShader(const std::string& name, std::string* error);
  Pipeline createGraphicsPipeline(const char* vs, const char* fs, VkFormat colorFormat,
                                  VkSampleCountFlagBits samples, VkFormat depthFormat,
                                  bool depthTest, bool depthWrite, bool blendAdditive,
                                  VkPrimitiveTopology topology, bool vertexInput, int vertexStride,
                                  VkCullModeFlags cull, std::string* error);
  void transition(VkCommandBuffer cmd, Image& img, VkImageLayout newLayout);
  void beginRendering(VkCommandBuffer cmd, const Image* color, const Image* resolve, const Image* depth,
                      VkClearColorValue clearColor, bool clearColorFlag, float clearDepth,
                      bool clearDepthFlag, VkRect2D area);
  void fullscreenDraw(VkCommandBuffer cmd, const Pipeline& p, VkDescriptorSet set,
                      const void* pushConstants, uint32_t pushSize);

  RenderConfig cfg_;
  std::string deviceName_;
  uint32_t maxSamples_ = 1;

  VkInstance instance_ = VK_NULL_HANDLE;
  VkPhysicalDevice physical_ = VK_NULL_HANDLE;
  VkDevice device_ = VK_NULL_HANDLE;
  VkQueue queue_ = VK_NULL_HANDLE;
  uint32_t queueFamily_ = 0;
  VkPhysicalDeviceMemoryProperties memProps_{};
  VkCommandPool cmdPool_ = VK_NULL_HANDLE;
  VkCommandBuffer cmd_ = VK_NULL_HANDLE;
  VkFence fence_ = VK_NULL_HANDLE;
  VkDescriptorSetLayout setLayout_ = VK_NULL_HANDLE;
  VkDescriptorPool descPool_ = VK_NULL_HANDLE;
  VkSampler linearSampler_ = VK_NULL_HANDLE;
  VkSampler shadowSampler_ = VK_NULL_HANDLE;

  Buffer ubo_{};
  Buffer bodyVB_{}, bodyIB_{};
  Buffer eyeVB_{}, eyeIB_{};
  Buffer hairVB_{}, hairIB_{};
  Buffer readbackBuf_{};
  uint32_t bodyIndexCount_ = 0;
  uint32_t eyeIndexCount_ = 0;
  uint32_t hairIndexCount_ = 0;
  uint32_t hairVertexCount_ = 0;
  VkDeviceSize hairVBCapacity_ = 0;

  Image shadowMap_{}, msaaColor_{}, hdrColor_{}, msaaDepth_{}, ldrColor_{}, finalColor_{}, dummyTex_{};
  std::vector<Image> bloom_;

  Pipeline skyPipe_{}, groundPipe_{}, bodyPipe_{}, eyePipe_{}, hairPipe_{};
  Pipeline shadowBodyPipe_{}, shadowHairPipe_{};
  Pipeline brightPipe_{}, downPipe_{}, compositePipe_{}, fxaaPipe_{};

  VkDescriptorSet setScene_ = VK_NULL_HANDLE;
  VkDescriptorSet setBright_ = VK_NULL_HANDLE;
  VkDescriptorSet setComposite_ = VK_NULL_HANDLE;
  VkDescriptorSet setFxaa_ = VK_NULL_HANDLE;
  std::vector<VkDescriptorSet> setDown_;

  std::vector<uint8_t> readbackPixels_;
  double lastRenderMs_ = 0.0;
  FrameUniforms* mappedUbo_ = nullptr;
};

}  // namespace hv
