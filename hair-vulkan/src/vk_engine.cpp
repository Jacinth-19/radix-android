#include "vk_engine.hpp"

#include <algorithm>
#include <chrono>
#include <cstdio>
#include <cstring>
#include <fstream>

namespace hv {
namespace {

#define VKOK(call)                                                                        \
  do {                                                                                    \
    VkResult vkres_ = (call);                                                             \
    if (vkres_ != VK_SUCCESS) {                                                           \
      if (error)                                                                          \
        *error = std::string("Vulkan error ") + std::to_string((int)vkres_) + " in " #call; \
      return false;                                                                       \
    }                                                                                     \
  } while (0)

}  // namespace

uint32_t Renderer::findMemoryType(uint32_t typeFilter, VkMemoryPropertyFlags props) const {
  for (uint32_t i = 0; i < memProps_.memoryTypeCount; ++i) {
    if ((typeFilter & (1u << i)) && (memProps_.memoryTypes[i].propertyFlags & props) == props) return i;
  }
  return 0;
}

bool Renderer::createInstance(std::string* error) {
  VkApplicationInfo app{VK_STRUCTURE_TYPE_APPLICATION_INFO};
  app.pApplicationName = "hair-vulkan";
  app.applicationVersion = VK_MAKE_VERSION(1, 0, 0);
  app.pEngineName = "hair-vulkan";
  app.engineVersion = VK_MAKE_VERSION(1, 0, 0);
  app.apiVersion = VK_API_VERSION_1_3;

  VkInstanceCreateInfo ci{VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO};
  ci.pApplicationInfo = &app;
  VKOK(vkCreateInstance(&ci, nullptr, &instance_));
  return true;
}

bool Renderer::pickDevice(std::string* error) {
  uint32_t count = 0;
  VKOK(vkEnumeratePhysicalDevices(instance_, &count, nullptr));
  if (count == 0) {
    if (error) *error = "no Vulkan physical device found (is a driver/ICD installed?)";
    return false;
  }
  std::vector<VkPhysicalDevice> devices(count);
  VKOK(vkEnumeratePhysicalDevices(instance_, &count, devices.data()));

  for (VkPhysicalDevice d : devices) {
    uint32_t qCount = 0;
    vkGetPhysicalDeviceQueueFamilyProperties(d, &qCount, nullptr);
    std::vector<VkQueueFamilyProperties> queues(qCount);
    vkGetPhysicalDeviceQueueFamilyProperties(d, &qCount, queues.data());
    for (uint32_t i = 0; i < qCount; ++i) {
      if ((queues[i].queueFlags & VK_QUEUE_GRAPHICS_BIT) && queues[i].queueCount > 0) {
        physical_ = d;
        queueFamily_ = i;
        break;
      }
    }
    if (physical_ != VK_NULL_HANDLE) break;
  }
  if (physical_ == VK_NULL_HANDLE) {
    if (error) *error = "no graphics queue family found";
    return false;
  }

  VkPhysicalDeviceProperties props{};
  vkGetPhysicalDeviceProperties(physical_, &props);
  deviceName_ = props.deviceName;
  maxSamples_ = 1;
  VkSampleCountFlags counts =
      props.limits.framebufferColorSampleCounts & props.limits.framebufferDepthSampleCounts;
  if (counts & VK_SAMPLE_COUNT_4_BIT) maxSamples_ = 4;
  else if (counts & VK_SAMPLE_COUNT_2_BIT) maxSamples_ = 2;
  vkGetPhysicalDeviceMemoryProperties(physical_, &memProps_);
  return true;
}

bool Renderer::createDevice(std::string* error) {
  float priority = 1.0f;
  VkDeviceQueueCreateInfo qci{VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO};
  qci.queueFamilyIndex = queueFamily_;
  qci.queueCount = 1;
  qci.pQueuePriorities = &priority;

  VkPhysicalDeviceVulkan13Features features13{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_3_FEATURES};
  features13.dynamicRendering = VK_TRUE;
  features13.synchronization2 = VK_TRUE;

  VkPhysicalDeviceFeatures features{};

  VkDeviceCreateInfo ci{VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO};
  ci.pNext = &features13;
  ci.queueCreateInfoCount = 1;
  ci.pQueueCreateInfos = &qci;
  ci.pEnabledFeatures = &features;
  VKOK(vkCreateDevice(physical_, &ci, nullptr, &device_));
  vkGetDeviceQueue(device_, queueFamily_, 0, &queue_);
  return true;
}

bool Renderer::createImage(Image& img, uint32_t w, uint32_t h, VkFormat format,
                           VkSampleCountFlagBits samples, VkImageUsageFlags usage,
                           VkImageAspectFlags aspect, std::string* error) {
  img.width = w;
  img.height = h;
  img.format = format;
  img.aspect = aspect;
  img.layout = VK_IMAGE_LAYOUT_UNDEFINED;

  VkImageCreateInfo ci{VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO};
  ci.imageType = VK_IMAGE_TYPE_2D;
  ci.format = format;
  ci.extent = {w, h, 1};
  ci.mipLevels = 1;
  ci.arrayLayers = 1;
  ci.samples = samples;
  ci.tiling = VK_IMAGE_TILING_OPTIMAL;
  ci.usage = usage;
  ci.sharingMode = VK_SHARING_MODE_EXCLUSIVE;
  ci.initialLayout = VK_IMAGE_LAYOUT_UNDEFINED;
  VKOK(vkCreateImage(device_, &ci, nullptr, &img.image));

  VkMemoryRequirements req{};
  vkGetImageMemoryRequirements(device_, img.image, &req);
  VkMemoryAllocateInfo ai{VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO};
  ai.allocationSize = req.size;
  ai.memoryTypeIndex = findMemoryType(req.memoryTypeBits, VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT);
  VKOK(vkAllocateMemory(device_, &ai, nullptr, &img.memory));
  VKOK(vkBindImageMemory(device_, img.image, img.memory, 0));

  VkImageViewCreateInfo vi{VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO};
  vi.image = img.image;
  vi.viewType = VK_IMAGE_VIEW_TYPE_2D;
  vi.format = format;
  vi.subresourceRange = {aspect, 0, 1, 0, 1};
  VKOK(vkCreateImageView(device_, &vi, nullptr, &img.view));
  return true;
}

bool Renderer::createBuffer(Buffer& buf, VkDeviceSize size, VkBufferUsageFlags usage,
                            VkMemoryPropertyFlags props, const void* data, std::string* error) {
  buf.size = size;
  VkBufferCreateInfo ci{VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO};
  ci.size = size;
  ci.usage = usage;
  ci.sharingMode = VK_SHARING_MODE_EXCLUSIVE;
  VKOK(vkCreateBuffer(device_, &ci, nullptr, &buf.buffer));
  VkMemoryRequirements req{};
  vkGetBufferMemoryRequirements(device_, buf.buffer, &req);
  VkMemoryAllocateInfo ai{VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO};
  ai.allocationSize = req.size;
  ai.memoryTypeIndex = findMemoryType(req.memoryTypeBits, props);
  VKOK(vkAllocateMemory(device_, &ai, nullptr, &buf.memory));
  VKOK(vkBindBufferMemory(device_, buf.buffer, buf.memory, 0));

  const bool hostVisible = (props & VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT) != 0;
  if (hostVisible) {
    VKOK(vkMapMemory(device_, buf.memory, 0, size, 0, &buf.mapped));
    if (data) std::memcpy(buf.mapped, data, (size_t)size);
  } else if (data) {
    Buffer staging{};
    if (!createBuffer(staging, size, VK_BUFFER_USAGE_TRANSFER_SRC_BIT,
                      VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT, data,
                      error))
      return false;
    VkCommandBufferAllocateInfo cai{VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO};
    cai.commandPool = cmdPool_;
    cai.level = VK_COMMAND_BUFFER_LEVEL_PRIMARY;
    cai.commandBufferCount = 1;
    VkCommandBuffer cmd;
    VKOK(vkAllocateCommandBuffers(device_, &cai, &cmd));
    VkCommandBufferBeginInfo bi{VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO};
    bi.flags = VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT;
    vkBeginCommandBuffer(cmd, &bi);
    VkBufferCopy region{0, 0, size};
    vkCmdCopyBuffer(cmd, staging.buffer, buf.buffer, 1, &region);
    vkEndCommandBuffer(cmd);
    VkSubmitInfo si{VK_STRUCTURE_TYPE_SUBMIT_INFO};
    si.commandBufferCount = 1;
    si.pCommandBuffers = &cmd;
    vkQueueSubmit(queue_, 1, &si, VK_NULL_HANDLE);
    vkQueueWaitIdle(queue_);
    vkFreeCommandBuffers(device_, cmdPool_, 1, &cmd);
    destroyBuffer(staging);
  }
  return true;
}

void Renderer::destroyBuffer(Buffer& buf) {
  if (buf.buffer) vkDestroyBuffer(device_, buf.buffer, nullptr);
  if (buf.memory) vkFreeMemory(device_, buf.memory, nullptr);
  buf = {};
}

VkShaderModule Renderer::loadShader(const std::string& name, std::string* error) {
  std::string path = cfg_.spirvDir + "/" + name + ".spv";
  std::ifstream f(path, std::ios::binary | std::ios::ate);
  if (!f) {
    if (error) *error = "cannot open SPIR-V shader: " + path;
    return VK_NULL_HANDLE;
  }
  std::streamsize size = f.tellg();
  f.seekg(0, std::ios::beg);
  std::vector<char> data((size_t)size);
  if (!f.read(data.data(), size)) {
    if (error) *error = "cannot read SPIR-V shader: " + path;
    return VK_NULL_HANDLE;
  }
  VkShaderModuleCreateInfo ci{VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO};
  ci.codeSize = (size_t)size;
  ci.pCode = reinterpret_cast<const uint32_t*>(data.data());
  VkShaderModule module = VK_NULL_HANDLE;
  if (vkCreateShaderModule(device_, &ci, nullptr, &module) != VK_SUCCESS) {
    if (error) *error = "vkCreateShaderModule failed for " + path;
    return VK_NULL_HANDLE;
  }
  return module;
}

Renderer::Pipeline Renderer::createGraphicsPipeline(const char* vs, const char* fs, VkFormat colorFormat,
                                                    VkSampleCountFlagBits samples, VkFormat depthFormat,
                                                    bool depthTest, bool depthWrite, bool blendAdditive,
                                                    VkPrimitiveTopology topology, bool vertexInput,
                                                    int vertexStride, VkCullModeFlags cull,
                                                    std::string* error) {
  Pipeline p{};
  VkShaderModule vsMod = loadShader(vs, error);
  if (!vsMod) return p;
  VkShaderModule fsMod = fs ? loadShader(fs, error) : VK_NULL_HANDLE;
  if (fs && !fsMod) return p;

  VkPipelineShaderStageCreateInfo stages[2]{};
  stages[0].sType = VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO;
  stages[0].stage = VK_SHADER_STAGE_VERTEX_BIT;
  stages[0].module = vsMod;
  stages[0].pName = "main";
  stages[1].sType = VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO;
  stages[1].stage = VK_SHADER_STAGE_FRAGMENT_BIT;
  stages[1].module = fsMod;
  stages[1].pName = "main";

  VkVertexInputBindingDescription binding{0, (uint32_t)vertexStride, VK_VERTEX_INPUT_RATE_VERTEX};
  VkVertexInputAttributeDescription attrs[5]{};
  uint32_t attrCount = 0;
  if (vertexInput) {
    if (vertexStride == (int)sizeof(MeshVertex)) {
      attrs[0] = {0, 0, VK_FORMAT_R32G32B32_SFLOAT, 0};
      attrs[1] = {1, 0, VK_FORMAT_R32G32B32_SFLOAT, 12};
      attrs[2] = {2, 0, VK_FORMAT_R32G32_SFLOAT, 24};
      attrCount = 3;
    } else {
      attrs[0] = {0, 0, VK_FORMAT_R32G32B32_SFLOAT, 0};
      attrs[1] = {1, 0, VK_FORMAT_R32G32B32_SFLOAT, 12};
      attrs[2] = {2, 0, VK_FORMAT_R32_SFLOAT, 24};
      attrs[3] = {3, 0, VK_FORMAT_R32_SFLOAT, 28};
      attrs[4] = {4, 0, VK_FORMAT_R32_SFLOAT, 32};
      attrCount = 5;
    }
  }

  VkPipelineVertexInputStateCreateInfo vin{VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO};
  vin.vertexBindingDescriptionCount = vertexInput ? 1u : 0u;
  vin.pVertexBindingDescriptions = vertexInput ? &binding : nullptr;
  vin.vertexAttributeDescriptionCount = attrCount;
  vin.pVertexAttributeDescriptions = attrs;

  VkPipelineInputAssemblyStateCreateInfo ia{VK_STRUCTURE_TYPE_PIPELINE_INPUT_ASSEMBLY_STATE_CREATE_INFO};
  ia.topology = topology;
  ia.primitiveRestartEnable = VK_FALSE;

  VkPipelineViewportStateCreateInfo vp{VK_STRUCTURE_TYPE_PIPELINE_VIEWPORT_STATE_CREATE_INFO};
  vp.viewportCount = 1;
  vp.scissorCount = 1;

  VkPipelineRasterizationStateCreateInfo rs{VK_STRUCTURE_TYPE_PIPELINE_RASTERIZATION_STATE_CREATE_INFO};
  rs.polygonMode = VK_POLYGON_MODE_FILL;
  rs.cullMode = cull;
  rs.frontFace = VK_FRONT_FACE_COUNTER_CLOCKWISE;
  rs.depthClampEnable = VK_FALSE;
  rs.rasterizerDiscardEnable = VK_FALSE;
  rs.depthBiasEnable = VK_FALSE;
  rs.lineWidth = 1.0f;

  VkPipelineMultisampleStateCreateInfo ms{VK_STRUCTURE_TYPE_PIPELINE_MULTISAMPLE_STATE_CREATE_INFO};
  ms.rasterizationSamples = samples;
  ms.sampleShadingEnable = VK_FALSE;

  VkPipelineDepthStencilStateCreateInfo dss{VK_STRUCTURE_TYPE_PIPELINE_DEPTH_STENCIL_STATE_CREATE_INFO};
  dss.depthTestEnable = depthTest ? VK_TRUE : VK_FALSE;
  dss.depthWriteEnable = depthWrite ? VK_TRUE : VK_FALSE;
  dss.depthCompareOp = VK_COMPARE_OP_LESS_OR_EQUAL;
  dss.depthBoundsTestEnable = VK_FALSE;
  dss.stencilTestEnable = VK_FALSE;

  VkPipelineColorBlendAttachmentState cba{};
  cba.blendEnable = blendAdditive ? VK_TRUE : VK_FALSE;
  cba.srcColorBlendFactor = VK_BLEND_FACTOR_ONE;
  cba.dstColorBlendFactor = VK_BLEND_FACTOR_ONE;
  cba.colorBlendOp = VK_BLEND_OP_ADD;
  cba.srcAlphaBlendFactor = VK_BLEND_FACTOR_ONE;
  cba.dstAlphaBlendFactor = VK_BLEND_FACTOR_ONE;
  cba.alphaBlendOp = VK_BLEND_OP_ADD;
  cba.colorWriteMask = VK_COLOR_COMPONENT_R_BIT | VK_COLOR_COMPONENT_G_BIT |
                       VK_COLOR_COMPONENT_B_BIT | VK_COLOR_COMPONENT_A_BIT;

  VkPipelineColorBlendStateCreateInfo cb{VK_STRUCTURE_TYPE_PIPELINE_COLOR_BLEND_STATE_CREATE_INFO};
  cb.attachmentCount = 1;
  cb.pAttachments = &cba;

  VkDynamicState dynStates[] = {VK_DYNAMIC_STATE_VIEWPORT, VK_DYNAMIC_STATE_SCISSOR};
  VkPipelineDynamicStateCreateInfo dyn{VK_STRUCTURE_TYPE_PIPELINE_DYNAMIC_STATE_CREATE_INFO};
  dyn.dynamicStateCount = 2;
  dyn.pDynamicStates = dynStates;

  VkPushConstantRange pcRange{};
  pcRange.stageFlags = VK_SHADER_STAGE_VERTEX_BIT | VK_SHADER_STAGE_FRAGMENT_BIT;
  pcRange.offset = 0;
  pcRange.size = 16;

  VkPipelineLayoutCreateInfo lci{VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO};
  lci.setLayoutCount = 1;
  lci.pSetLayouts = &setLayout_;
  lci.pushConstantRangeCount = 1;
  lci.pPushConstantRanges = &pcRange;
  if (vkCreatePipelineLayout(device_, &lci, nullptr, &p.layout) != VK_SUCCESS) {
    if (error) *error = "vkCreatePipelineLayout failed";
    return Pipeline{};
  }

  VkPipelineRenderingCreateInfo prc{VK_STRUCTURE_TYPE_PIPELINE_RENDERING_CREATE_INFO};
  prc.colorAttachmentCount = (colorFormat == VK_FORMAT_UNDEFINED) ? 0u : 1u;
  prc.pColorAttachmentFormats = (colorFormat == VK_FORMAT_UNDEFINED) ? nullptr : &colorFormat;
  prc.depthAttachmentFormat = depthFormat;

  VkGraphicsPipelineCreateInfo gp{VK_STRUCTURE_TYPE_GRAPHICS_PIPELINE_CREATE_INFO};
  gp.pNext = &prc;
  gp.stageCount = fs ? 2u : 1u;
  gp.pStages = stages;
  gp.pVertexInputState = &vin;
  gp.pInputAssemblyState = &ia;
  gp.pViewportState = &vp;
  gp.pRasterizationState = &rs;
  gp.pMultisampleState = &ms;
  gp.pDepthStencilState = &dss;
  gp.pColorBlendState = &cb;
  gp.pDynamicState = &dyn;
  gp.layout = p.layout;
  gp.renderPass = VK_NULL_HANDLE;

  if (vkCreateGraphicsPipelines(device_, VK_NULL_HANDLE, 1, &gp, nullptr, &p.pipeline) != VK_SUCCESS) {
    if (error)
      *error = std::string("vkCreateGraphicsPipelines failed for ") + vs + " / " + (fs ? fs : "");
    return Pipeline{};
  }
  vkDestroyShaderModule(device_, vsMod, nullptr);
  if (fsMod) vkDestroyShaderModule(device_, fsMod, nullptr);
  return p;
}

void Renderer::transition(VkCommandBuffer cmd, Image& img, VkImageLayout newLayout) {
  if (img.layout == newLayout) return;
  VkImageMemoryBarrier b{VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER};
  b.oldLayout = img.layout;
  b.newLayout = newLayout;
  b.srcQueueFamilyIndex = VK_QUEUE_FAMILY_IGNORED;
  b.dstQueueFamilyIndex = VK_QUEUE_FAMILY_IGNORED;
  b.image = img.image;
  b.subresourceRange = {img.aspect, 0, 1, 0, 1};
  b.srcAccessMask = VK_ACCESS_MEMORY_READ_BIT | VK_ACCESS_MEMORY_WRITE_BIT;
  b.dstAccessMask = VK_ACCESS_MEMORY_READ_BIT | VK_ACCESS_MEMORY_WRITE_BIT;
  VkPipelineStageFlags srcStage = (img.layout == VK_IMAGE_LAYOUT_UNDEFINED)
                                      ? VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT
                                      : VK_PIPELINE_STAGE_ALL_COMMANDS_BIT;
  if (img.layout == VK_IMAGE_LAYOUT_UNDEFINED) b.srcAccessMask = 0;
  vkCmdPipelineBarrier(cmd, srcStage, VK_PIPELINE_STAGE_ALL_COMMANDS_BIT, 0, 0, nullptr, 0, nullptr, 1,
                       &b);
  img.layout = newLayout;
}

void Renderer::beginRendering(VkCommandBuffer cmd, const Image* color, const Image* resolve,
                              const Image* depth, VkClearColorValue clearColor, bool clearColorFlag,
                              float clearDepth, bool clearDepthFlag, VkRect2D area) {
  VkRenderingAttachmentInfo ca{VK_STRUCTURE_TYPE_RENDERING_ATTACHMENT_INFO};
  VkRenderingAttachmentInfo da{VK_STRUCTURE_TYPE_RENDERING_ATTACHMENT_INFO};
  VkRenderingInfo ri{VK_STRUCTURE_TYPE_RENDERING_INFO};
  ri.renderArea = area;
  ri.layerCount = 1;
  if (color) {
    ca.imageView = color->view;
    ca.imageLayout = VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL;
    ca.loadOp = clearColorFlag ? VK_ATTACHMENT_LOAD_OP_CLEAR : VK_ATTACHMENT_LOAD_OP_LOAD;
    ca.storeOp = VK_ATTACHMENT_STORE_OP_STORE;
    ca.clearValue.color = clearColor;
    if (resolve) {
      ca.resolveMode = VK_RESOLVE_MODE_AVERAGE_BIT;
      ca.resolveImageView = resolve->view;
      ca.resolveImageLayout = VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL;
    }
    ri.colorAttachmentCount = 1;
    ri.pColorAttachments = &ca;
  }
  if (depth) {
    da.imageView = depth->view;
    da.imageLayout = VK_IMAGE_LAYOUT_DEPTH_ATTACHMENT_OPTIMAL;
    da.loadOp = clearDepthFlag ? VK_ATTACHMENT_LOAD_OP_CLEAR : VK_ATTACHMENT_LOAD_OP_LOAD;
    da.storeOp = VK_ATTACHMENT_STORE_OP_STORE;
    da.clearValue.depthStencil = {clearDepth, 0};
    ri.pDepthAttachment = &da;
  }
  vkCmdBeginRendering(cmd, &ri);
}

void Renderer::fullscreenDraw(VkCommandBuffer cmd, const Pipeline& p, VkDescriptorSet set,
                              const void* pushConstants, uint32_t pushSize) {
  vkCmdBindPipeline(cmd, VK_PIPELINE_BIND_POINT_GRAPHICS, p.pipeline);
  vkCmdBindDescriptorSets(cmd, VK_PIPELINE_BIND_POINT_GRAPHICS, p.layout, 0, 1, &set, 0, nullptr);
  if (pushConstants && pushSize) {
    vkCmdPushConstants(cmd, p.layout, VK_SHADER_STAGE_VERTEX_BIT | VK_SHADER_STAGE_FRAGMENT_BIT, 0,
                       pushSize, pushConstants);
  }
  vkCmdDraw(cmd, 3, 1, 0, 0);
}

bool Renderer::createResources(std::string* error) {
  const VkSampleCountFlagBits samples = (VkSampleCountFlagBits)cfg_.msaa;
  const VkFormat hdr = VK_FORMAT_R16G16B16A16_SFLOAT;

  if (!createImage(shadowMap_, (uint32_t)cfg_.shadowSize, (uint32_t)cfg_.shadowSize,
                   VK_FORMAT_D32_SFLOAT, VK_SAMPLE_COUNT_1_BIT,
                   VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT | VK_IMAGE_USAGE_SAMPLED_BIT,
                   VK_IMAGE_ASPECT_DEPTH_BIT, error))
    return false;

  if (!createImage(msaaColor_, (uint32_t)cfg_.width, (uint32_t)cfg_.height, hdr, samples,
                   VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT | VK_IMAGE_USAGE_TRANSFER_SRC_BIT,
                   VK_IMAGE_ASPECT_COLOR_BIT, error))
    return false;

  if (!createImage(msaaDepth_, (uint32_t)cfg_.width, (uint32_t)cfg_.height, VK_FORMAT_D32_SFLOAT,
                   samples, VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT, VK_IMAGE_ASPECT_DEPTH_BIT,
                   error))
    return false;

  if (!createImage(hdrColor_, (uint32_t)cfg_.width, (uint32_t)cfg_.height, hdr, VK_SAMPLE_COUNT_1_BIT,
                   VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT | VK_IMAGE_USAGE_SAMPLED_BIT |
                       VK_IMAGE_USAGE_TRANSFER_DST_BIT,
                   VK_IMAGE_ASPECT_COLOR_BIT, error))
    return false;

  if (!createImage(ldrColor_, (uint32_t)cfg_.width, (uint32_t)cfg_.height, VK_FORMAT_R8G8B8A8_UNORM,
                   VK_SAMPLE_COUNT_1_BIT,
                   VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT | VK_IMAGE_USAGE_SAMPLED_BIT,
                   VK_IMAGE_ASPECT_COLOR_BIT, error))
    return false;

  if (!createImage(finalColor_, (uint32_t)cfg_.width, (uint32_t)cfg_.height, VK_FORMAT_R8G8B8A8_UNORM,
                   VK_SAMPLE_COUNT_1_BIT,
                   VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT | VK_IMAGE_USAGE_SAMPLED_BIT |
                       VK_IMAGE_USAGE_TRANSFER_SRC_BIT,
                   VK_IMAGE_ASPECT_COLOR_BIT, error))
    return false;

  bloom_.resize((size_t)cfg_.bloomLevels);
  uint32_t bw = (uint32_t)cfg_.width, bh = (uint32_t)cfg_.height;
  for (int i = 0; i < cfg_.bloomLevels; ++i) {
    bw = std::max(1u, bw / 2);
    bh = std::max(1u, bh / 2);
    if (!createImage(bloom_[(size_t)i], bw, bh, hdr, VK_SAMPLE_COUNT_1_BIT,
                     VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT | VK_IMAGE_USAGE_SAMPLED_BIT,
                     VK_IMAGE_ASPECT_COLOR_BIT, error))
      return false;
  }

  // 1x1 dummy texture bound to unused sampler slots.
  const uint8_t white[4] = {0, 0, 0, 255};
  if (!createImage(dummyTex_, 1, 1, VK_FORMAT_R8G8B8A8_UNORM, VK_SAMPLE_COUNT_1_BIT,
                   VK_IMAGE_USAGE_SAMPLED_BIT | VK_IMAGE_USAGE_TRANSFER_DST_BIT,
                   VK_IMAGE_ASPECT_COLOR_BIT, error))
    return false;

  if (!createBuffer(ubo_, (VkDeviceSize)((sizeof(FrameUniforms) + 255) / 256 * 256),
                    VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT,
                    VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT,
                    nullptr, error))
    return false;
  mappedUbo_ = reinterpret_cast<FrameUniforms*>(ubo_.mapped);

  if (!createBuffer(readbackBuf_, (VkDeviceSize)cfg_.width * cfg_.height * 4,
                    VK_BUFFER_USAGE_TRANSFER_DST_BIT,
                    VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT,
                    nullptr, error))
    return false;
  readbackPixels_.resize((size_t)cfg_.width * cfg_.height * 4);

  VkSamplerCreateInfo sci{VK_STRUCTURE_TYPE_SAMPLER_CREATE_INFO};
  sci.magFilter = VK_FILTER_LINEAR;
  sci.minFilter = VK_FILTER_LINEAR;
  sci.mipmapMode = VK_SAMPLER_MIPMAP_MODE_NEAREST;
  sci.addressModeU = VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE;
  sci.addressModeV = VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE;
  sci.addressModeW = VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE;
  sci.maxLod = 1.0f;
  VKOK(vkCreateSampler(device_, &sci, nullptr, &linearSampler_));

  VkSamplerCreateInfo ssi{VK_STRUCTURE_TYPE_SAMPLER_CREATE_INFO};
  ssi.magFilter = VK_FILTER_LINEAR;
  ssi.minFilter = VK_FILTER_LINEAR;
  ssi.mipmapMode = VK_SAMPLER_MIPMAP_MODE_NEAREST;
  ssi.addressModeU = VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_BORDER;
  ssi.addressModeV = VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_BORDER;
  ssi.addressModeW = VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_BORDER;
  ssi.borderColor = VK_BORDER_COLOR_FLOAT_OPAQUE_WHITE;
  ssi.compareEnable = VK_TRUE;
  ssi.compareOp = VK_COMPARE_OP_LESS_OR_EQUAL;
  ssi.maxLod = 1.0f;
  VKOK(vkCreateSampler(device_, &ssi, nullptr, &shadowSampler_));

  // Clear the dummy texture to black.
  {
    VkCommandBufferAllocateInfo cai{VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO};
    cai.commandPool = cmdPool_;
    cai.level = VK_COMMAND_BUFFER_LEVEL_PRIMARY;
    cai.commandBufferCount = 1;
    VkCommandBuffer cmd;
    VKOK(vkAllocateCommandBuffers(device_, &cai, &cmd));
    VkCommandBufferBeginInfo bi{VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO};
    bi.flags = VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT;
    vkBeginCommandBuffer(cmd, &bi);
    transition(cmd, dummyTex_, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL);
    VkClearColorValue cc{};
    cc.float32[3] = 1.0f;
    VkImageSubresourceRange range{VK_IMAGE_ASPECT_COLOR_BIT, 0, 1, 0, 1};
    vkCmdClearColorImage(cmd, dummyTex_.image, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, &cc, 1, &range);
    transition(cmd, dummyTex_, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL);
    vkEndCommandBuffer(cmd);
    VkSubmitInfo si{VK_STRUCTURE_TYPE_SUBMIT_INFO};
    si.commandBufferCount = 1;
    si.pCommandBuffers = &cmd;
    vkQueueSubmit(queue_, 1, &si, VK_NULL_HANDLE);
    vkQueueWaitIdle(queue_);
    vkFreeCommandBuffers(device_, cmdPool_, 1, &cmd);
  }
  (void)white;
  return true;
}

bool Renderer::createDescriptors(std::string* error) {
  VkDescriptorSetLayoutBinding bindings[6]{};
  bindings[0].binding = 0;
  bindings[0].descriptorType = VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER;
  bindings[0].descriptorCount = 1;
  bindings[0].stageFlags = VK_SHADER_STAGE_VERTEX_BIT | VK_SHADER_STAGE_FRAGMENT_BIT;
  bindings[1].binding = 1;
  bindings[1].descriptorType = VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER;
  bindings[1].descriptorCount = 1;
  bindings[1].stageFlags = VK_SHADER_STAGE_FRAGMENT_BIT;
  bindings[2].binding = 2;
  bindings[2].descriptorType = VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER;
  bindings[2].descriptorCount = 1;
  bindings[2].stageFlags = VK_SHADER_STAGE_FRAGMENT_BIT;
  bindings[3].binding = 3;
  bindings[3].descriptorType = VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER;
  bindings[3].descriptorCount = 1;
  bindings[3].stageFlags = VK_SHADER_STAGE_FRAGMENT_BIT;
  bindings[4].binding = 4;
  bindings[4].descriptorType = VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER;
  bindings[4].descriptorCount = 1;
  bindings[4].stageFlags = VK_SHADER_STAGE_FRAGMENT_BIT;
  bindings[5].binding = 5;
  bindings[5].descriptorType = VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER;
  bindings[5].descriptorCount = 1;
  bindings[5].stageFlags = VK_SHADER_STAGE_FRAGMENT_BIT;

  VkDescriptorSetLayoutCreateInfo lci{VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_CREATE_INFO};
  lci.bindingCount = 6;
  lci.pBindings = bindings;
  VKOK(vkCreateDescriptorSetLayout(device_, &lci, nullptr, &setLayout_));

  const uint32_t setCount = 5u + (uint32_t)cfg_.bloomLevels;
  VkDescriptorPoolSize sizes[2]{};
  sizes[0].type = VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER;
  sizes[0].descriptorCount = setCount;
  sizes[1].type = VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER;
  sizes[1].descriptorCount = setCount * 5;
  VkDescriptorPoolCreateInfo pci{VK_STRUCTURE_TYPE_DESCRIPTOR_POOL_CREATE_INFO};
  pci.maxSets = setCount;
  pci.poolSizeCount = 2;
  pci.pPoolSizes = sizes;
  VKOK(vkCreateDescriptorPool(device_, &pci, nullptr, &descPool_));

  std::vector<VkDescriptorSetLayout> layouts(setCount, setLayout_);
  std::vector<VkDescriptorSet> sets(setCount);
  VkDescriptorSetAllocateInfo ai{VK_STRUCTURE_TYPE_DESCRIPTOR_SET_ALLOCATE_INFO};
  ai.descriptorPool = descPool_;
  ai.descriptorSetCount = setCount;
  ai.pSetLayouts = layouts.data();
  VKOK(vkAllocateDescriptorSets(device_, &ai, sets.data()));

  uint32_t cursor = 0;
  setScene_ = sets[cursor++];
  setBright_ = sets[cursor++];
  setComposite_ = sets[cursor++];
  setFxaa_ = sets[cursor++];
  cursor++;  // reserved
  setDown_.resize((size_t)cfg_.bloomLevels);
  for (int i = 0; i < cfg_.bloomLevels; ++i) setDown_[(size_t)i] = sets[cursor++];

  auto writeSet = [&](VkDescriptorSet set, VkImageView tex0, VkImageView tex1, VkImageView tex2,
                      VkImageView tex3) {
    VkDescriptorBufferInfo bi{ubo_.buffer, 0, sizeof(FrameUniforms)};
    VkDescriptorImageInfo shadow{shadowSampler_, shadowMap_.view,
                                 VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL};
    VkImageView views[4] = {tex0 ? tex0 : dummyTex_.view, tex1 ? tex1 : dummyTex_.view,
                            tex2 ? tex2 : dummyTex_.view, tex3 ? tex3 : dummyTex_.view};
    VkDescriptorImageInfo images[4];
    for (int i = 0; i < 4; ++i) {
      images[i].sampler = linearSampler_;
      images[i].imageView = views[i];
      images[i].imageLayout = VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL;
    }
    VkWriteDescriptorSet writes[6]{};
    for (int i = 0; i < 6; ++i) {
      writes[i].sType = VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET;
      writes[i].dstSet = set;
      writes[i].dstBinding = (uint32_t)i;
      writes[i].descriptorCount = 1;
    }
    writes[0].descriptorType = VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER;
    writes[0].pBufferInfo = &bi;
    writes[1].descriptorType = VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER;
    writes[1].pImageInfo = &shadow;
    for (int i = 0; i < 4; ++i) {
      writes[2 + i].descriptorType = VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER;
      writes[2 + i].pImageInfo = &images[i];
    }
    vkUpdateDescriptorSets(device_, 6, writes, 0, nullptr);
  };

  writeSet(setScene_, nullptr, nullptr, nullptr, nullptr);
  writeSet(setBright_, hdrColor_.view, nullptr, nullptr, nullptr);
  writeSet(setComposite_, hdrColor_.view,
           bloom_.size() > 0 ? bloom_[0].view : nullptr,
           bloom_.size() > 1 ? bloom_[1].view : nullptr,
           bloom_.size() > 2 ? bloom_[2].view : nullptr);
  writeSet(setFxaa_, ldrColor_.view, nullptr, nullptr, nullptr);

  for (int i = 0; i < cfg_.bloomLevels; ++i) {
    VkImageView src = (i == 0) ? hdrColor_.view : bloom_[(size_t)(i - 1)].view;
    writeSet(setDown_[(size_t)i], src, nullptr, nullptr, nullptr);
  }
  return true;
}

bool Renderer::createPipelines(std::string* error) {
  const VkFormat hdr = VK_FORMAT_R16G16B16A16_SFLOAT;
  const VkFormat ldr = VK_FORMAT_R8G8B8A8_UNORM;
  const VkSampleCountFlagBits samples = (VkSampleCountFlagBits)cfg_.msaa;

  skyPipe_ = createGraphicsPipeline("fullscreen", "sky", hdr, samples, VK_FORMAT_D32_SFLOAT, false,
                                    false, false, VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST, false, 0,
                                    VK_CULL_MODE_NONE, error);
  if (!skyPipe_.pipeline) return false;

  groundPipe_ = createGraphicsPipeline("fullscreen", "ground", hdr, samples, VK_FORMAT_D32_SFLOAT,
                                       true, true, false, VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST, false, 0,
                                       VK_CULL_MODE_NONE, error);
  if (!groundPipe_.pipeline) return false;

  bodyPipe_ = createGraphicsPipeline("body", "body_frag", hdr, samples, VK_FORMAT_D32_SFLOAT, true, true,
                                     false, VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST, true,
                                     (int)sizeof(MeshVertex), VK_CULL_MODE_NONE, error);
  if (!bodyPipe_.pipeline) return false;

  hairPipe_ = createGraphicsPipeline("hair", "hair_frag", hdr, samples, VK_FORMAT_D32_SFLOAT, true, true,
                                     false, VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST, true,
                                     (int)sizeof(HairVertex), VK_CULL_MODE_NONE, error);
  if (!hairPipe_.pipeline) return false;

  shadowBodyPipe_ = createGraphicsPipeline("shadow_body", nullptr, VK_FORMAT_UNDEFINED,
                                           VK_SAMPLE_COUNT_1_BIT, VK_FORMAT_D32_SFLOAT, true, true,
                                           false, VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST, true,
                                           (int)sizeof(MeshVertex), VK_CULL_MODE_NONE, error);
  if (!shadowBodyPipe_.pipeline) return false;

  shadowHairPipe_ = createGraphicsPipeline("shadow_hair", nullptr, VK_FORMAT_UNDEFINED,
                                           VK_SAMPLE_COUNT_1_BIT, VK_FORMAT_D32_SFLOAT, true, true,
                                           false, VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST, true,
                                           (int)sizeof(HairVertex), VK_CULL_MODE_NONE, error);
  if (!shadowHairPipe_.pipeline) return false;

  brightPipe_ = createGraphicsPipeline("fullscreen", "bright", hdr, VK_SAMPLE_COUNT_1_BIT,
                                      VK_FORMAT_UNDEFINED, false, false, false,
                                      VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST, false, 0,
                                      VK_CULL_MODE_NONE, error);
  if (!brightPipe_.pipeline) return false;

  downPipe_ = createGraphicsPipeline("fullscreen", "downsample", hdr, VK_SAMPLE_COUNT_1_BIT,
                                    VK_FORMAT_UNDEFINED, false, false, false,
                                    VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST, false, 0,
                                    VK_CULL_MODE_NONE, error);
  if (!downPipe_.pipeline) return false;

  compositePipe_ = createGraphicsPipeline("fullscreen", "composite", ldr, VK_SAMPLE_COUNT_1_BIT,
                                         VK_FORMAT_UNDEFINED, false, false, false,
                                         VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST, false, 0,
                                         VK_CULL_MODE_NONE, error);
  if (!compositePipe_.pipeline) return false;

  fxaaPipe_ = createGraphicsPipeline("fullscreen", "fxaa", ldr, VK_SAMPLE_COUNT_1_BIT,
                                     VK_FORMAT_UNDEFINED, false, false, false,
                                     VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST, false, 0,
                                     VK_CULL_MODE_NONE, error);
  if (!fxaaPipe_.pipeline) return false;

  return true;
}

bool Renderer::init(const RenderConfig& cfg, std::string* error) {
  cfg_ = cfg;
  if (!createInstance(error)) return false;
  if (!pickDevice(error)) return false;
  // Only 1x and 4x are used: lavapipe's 2x MSAA path renders a black frame.
  if (cfg_.msaa > 1 && maxSamples_ < 4) cfg_.msaa = 1;
  else if (cfg_.msaa > 1 && cfg_.msaa < 4) cfg_.msaa = 4;

  if (!createDevice(error)) return false;

  VkCommandPoolCreateInfo pci{VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO};
  pci.flags = VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT;
  pci.queueFamilyIndex = queueFamily_;
  if (vkCreateCommandPool(device_, &pci, nullptr, &cmdPool_) != VK_SUCCESS) {
    if (error) *error = "vkCreateCommandPool failed";
    return false;
  }

  VkCommandBufferAllocateInfo cai{VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO};
  cai.commandPool = cmdPool_;
  cai.level = VK_COMMAND_BUFFER_LEVEL_PRIMARY;
  cai.commandBufferCount = 1;
  VKOK(vkAllocateCommandBuffers(device_, &cai, &cmd_));

  VkFenceCreateInfo fci{VK_STRUCTURE_TYPE_FENCE_CREATE_INFO};
  VKOK(vkCreateFence(device_, &fci, nullptr, &fence_));

  if (!createResources(error)) return false;
  if (!createDescriptors(error)) return false;
  if (!createPipelines(error)) return false;
  return true;
}

void Renderer::destroy() {
  if (device_) vkDeviceWaitIdle(device_);
  auto killImage = [&](Image& img) {
    if (img.view) vkDestroyImageView(device_, img.view, nullptr);
    if (img.image) vkDestroyImage(device_, img.image, nullptr);
    if (img.memory) vkFreeMemory(device_, img.memory, nullptr);
    img = {};
  };
  killImage(shadowMap_);
  killImage(msaaColor_);
  killImage(msaaDepth_);
  killImage(hdrColor_);
  killImage(ldrColor_);
  killImage(finalColor_);
  killImage(dummyTex_);
  for (Image& i : bloom_) killImage(i);
  bloom_.clear();

  auto killPipe = [&](Pipeline& p) {
    if (p.pipeline) vkDestroyPipeline(device_, p.pipeline, nullptr);
    if (p.layout) vkDestroyPipelineLayout(device_, p.layout, nullptr);
    p = {};
  };
  killPipe(skyPipe_);
  killPipe(groundPipe_);
  killPipe(bodyPipe_);
  killPipe(hairPipe_);
  killPipe(shadowBodyPipe_);
  killPipe(shadowHairPipe_);
  killPipe(brightPipe_);
  killPipe(downPipe_);
  killPipe(compositePipe_);
  killPipe(fxaaPipe_);

  destroyBuffer(ubo_);
  destroyBuffer(bodyVB_);
  destroyBuffer(bodyIB_);
  destroyBuffer(hairVB_);
  destroyBuffer(hairIB_);
  destroyBuffer(readbackBuf_);

  if (shadowSampler_) vkDestroySampler(device_, shadowSampler_, nullptr);
  if (linearSampler_) vkDestroySampler(device_, linearSampler_, nullptr);
  if (descPool_) vkDestroyDescriptorPool(device_, descPool_, nullptr);
  if (setLayout_) vkDestroyDescriptorSetLayout(device_, setLayout_, nullptr);
  if (fence_) vkDestroyFence(device_, fence_, nullptr);
  if (cmdPool_) vkDestroyCommandPool(device_, cmdPool_, nullptr);
  if (device_) vkDestroyDevice(device_, nullptr);
  if (instance_) vkDestroyInstance(instance_, nullptr);
  device_ = VK_NULL_HANDLE;
  instance_ = VK_NULL_HANDLE;
}

void Renderer::setBodyMesh(const Mesh& mesh) {
  destroyBuffer(bodyVB_);
  destroyBuffer(bodyIB_);
  std::string err;
  createBuffer(bodyVB_, (VkDeviceSize)(mesh.vertices.size() * sizeof(MeshVertex)),
               VK_BUFFER_USAGE_VERTEX_BUFFER_BIT | VK_BUFFER_USAGE_TRANSFER_DST_BIT,
               VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT, mesh.vertices.data(), &err);
  createBuffer(bodyIB_, (VkDeviceSize)(mesh.indices.size() * sizeof(uint32_t)),
               VK_BUFFER_USAGE_INDEX_BUFFER_BIT | VK_BUFFER_USAGE_TRANSFER_DST_BIT,
               VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT, mesh.indices.data(), &err);
  bodyIndexCount_ = (uint32_t)mesh.indices.size();
}

void Renderer::setHairGeometry(const std::vector<HairVertex>& verts,
                               const std::vector<uint32_t>& indices) {
  destroyBuffer(hairVB_);
  destroyBuffer(hairIB_);
  std::string err;
  hairVBCapacity_ = (VkDeviceSize)(verts.size() * sizeof(HairVertex));
  createBuffer(hairVB_, hairVBCapacity_,
               VK_BUFFER_USAGE_VERTEX_BUFFER_BIT | VK_BUFFER_USAGE_TRANSFER_DST_BIT,
               VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT,
               verts.data(), &err);
  createBuffer(hairIB_, (VkDeviceSize)(indices.size() * sizeof(uint32_t)),
               VK_BUFFER_USAGE_INDEX_BUFFER_BIT | VK_BUFFER_USAGE_TRANSFER_DST_BIT,
               VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT, indices.data(), &err);
  hairVertexCount_ = (uint32_t)verts.size();
  hairIndexCount_ = (uint32_t)indices.size();
}

void Renderer::updateHairVertices(const std::vector<HairVertex>& verts) {
  if (verts.empty() || !hairVB_.mapped) return;
  const VkDeviceSize bytes = (VkDeviceSize)(verts.size() * sizeof(HairVertex));
  std::memcpy(hairVB_.mapped, verts.data(), (size_t)std::min(bytes, hairVBCapacity_));
}

bool Renderer::render(const FrameUniforms& uniforms, std::string* error) {
  auto t0 = std::chrono::high_resolution_clock::now();
  std::memcpy(mappedUbo_, &uniforms, sizeof(FrameUniforms));

  VKOK(vkResetCommandBuffer(cmd_, 0));
  VkCommandBufferBeginInfo bi{VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO};
  bi.flags = VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT;
  VKOK(vkBeginCommandBuffer(cmd_, &bi));

  const VkRect2D fullRect{{0, 0}, {(uint32_t)cfg_.width, (uint32_t)cfg_.height}};
  const VkRect2D shadowRect{{0, 0}, {(uint32_t)cfg_.shadowSize, (uint32_t)cfg_.shadowSize}};
  VkViewport sceneViewport{0.0f, (float)cfg_.height, (float)cfg_.width, -(float)cfg_.height, 0.0f, 1.0f};
  VkViewport shadowViewport{0.0f, (float)cfg_.shadowSize, (float)cfg_.shadowSize,
                            -(float)cfg_.shadowSize, 0.0f, 1.0f};

  // ---------------------------------------------------------------- shadow --
  transition(cmd_, shadowMap_, VK_IMAGE_LAYOUT_DEPTH_ATTACHMENT_OPTIMAL);
  beginRendering(cmd_, nullptr, nullptr, &shadowMap_, VkClearColorValue{}, false, 1.0f, true,
                 shadowRect);
  vkCmdSetViewport(cmd_, 0, 1, &shadowViewport);
  vkCmdSetScissor(cmd_, 0, 1, &shadowRect);
  vkCmdBindDescriptorSets(cmd_, VK_PIPELINE_BIND_POINT_GRAPHICS, shadowBodyPipe_.layout, 0, 1,
                          &setScene_, 0, nullptr);
  vkCmdBindPipeline(cmd_, VK_PIPELINE_BIND_POINT_GRAPHICS, shadowBodyPipe_.pipeline);
  {
    VkDeviceSize offset = 0;
    vkCmdBindVertexBuffers(cmd_, 0, 1, &bodyVB_.buffer, &offset);
    vkCmdBindIndexBuffer(cmd_, bodyIB_.buffer, 0, VK_INDEX_TYPE_UINT32);
    vkCmdDrawIndexed(cmd_, bodyIndexCount_, 1, 0, 0, 0);
  }
  if (hairIndexCount_ > 0) {
    vkCmdBindPipeline(cmd_, VK_PIPELINE_BIND_POINT_GRAPHICS, shadowHairPipe_.pipeline);
    VkDeviceSize offset = 0;
    vkCmdBindVertexBuffers(cmd_, 0, 1, &hairVB_.buffer, &offset);
    vkCmdBindIndexBuffer(cmd_, hairIB_.buffer, 0, VK_INDEX_TYPE_UINT32);
    vkCmdDrawIndexed(cmd_, hairIndexCount_, 1, 0, 0, 0);
  }
  vkCmdEndRendering(cmd_);
  transition(cmd_, shadowMap_, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL);

  // ------------------------------------------------------------ scene pass --
  const bool msaa = cfg_.msaa > 1;
  Image& colorTarget = msaa ? msaaColor_ : hdrColor_;
  transition(cmd_, colorTarget, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);
  transition(cmd_, msaaDepth_, VK_IMAGE_LAYOUT_DEPTH_ATTACHMENT_OPTIMAL);
  if (msaa) transition(cmd_, hdrColor_, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);

  VkClearColorValue clear{};
  beginRendering(cmd_, &colorTarget, nullptr, &msaaDepth_, clear, true, 1.0f, true, fullRect);
  vkCmdSetViewport(cmd_, 0, 1, &sceneViewport);
  vkCmdSetScissor(cmd_, 0, 1, &fullRect);

  // Sky (no depth write, drawn first as the background).
  fullscreenDraw(cmd_, skyPipe_, setScene_, nullptr, 0);

  // Ground: an infinite plane reconstructed in the fragment shader.
  fullscreenDraw(cmd_, groundPipe_, setScene_, nullptr, 0);

  // Bust.
  if (drawBody) {
  vkCmdBindPipeline(cmd_, VK_PIPELINE_BIND_POINT_GRAPHICS, bodyPipe_.pipeline);
  vkCmdBindDescriptorSets(cmd_, VK_PIPELINE_BIND_POINT_GRAPHICS, bodyPipe_.layout, 0, 1, &setScene_, 0,
                          nullptr);
  {
    VkDeviceSize offset = 0;
    vkCmdBindVertexBuffers(cmd_, 0, 1, &bodyVB_.buffer, &offset);
    vkCmdBindIndexBuffer(cmd_, bodyIB_.buffer, 0, VK_INDEX_TYPE_UINT32);
    vkCmdDrawIndexed(cmd_, bodyIndexCount_, 1, 0, 0, 0);
  }
  }

  // Hair.
  if (drawHair && hairIndexCount_ > 0) {
    vkCmdBindPipeline(cmd_, VK_PIPELINE_BIND_POINT_GRAPHICS, hairPipe_.pipeline);
    vkCmdBindDescriptorSets(cmd_, VK_PIPELINE_BIND_POINT_GRAPHICS, hairPipe_.layout, 0, 1, &setScene_,
                            0, nullptr);
    VkDeviceSize offset = 0;
    vkCmdBindVertexBuffers(cmd_, 0, 1, &hairVB_.buffer, &offset);
    vkCmdBindIndexBuffer(cmd_, hairIB_.buffer, 0, VK_INDEX_TYPE_UINT32);
    vkCmdDrawIndexed(cmd_, hairIndexCount_, 1, 0, 0, 0);
  }
  vkCmdEndRendering(cmd_);

  if (msaa) {
    // Explicit MSAA resolve (more predictable across drivers than a resolve attachment).
    transition(cmd_, msaaColor_, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL);
    transition(cmd_, hdrColor_, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL);
    VkImageResolve resolve{};
    resolve.srcSubresource = {VK_IMAGE_ASPECT_COLOR_BIT, 0, 0, 1};
    resolve.dstSubresource = {VK_IMAGE_ASPECT_COLOR_BIT, 0, 0, 1};
    resolve.extent = {(uint32_t)cfg_.width, (uint32_t)cfg_.height, 1};
    vkCmdResolveImage(cmd_, msaaColor_.image, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL, hdrColor_.image,
                      VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, 1, &resolve);
  }
  transition(cmd_, hdrColor_, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL);

  // ----------------------------------------------------------------- bloom --
  if (cfg_.bloom && !bloom_.empty()) {
    // Bright pass into bloom[0].
    transition(cmd_, bloom_[0], VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);
    VkRect2D r0{{0, 0}, {bloom_[0].width, bloom_[0].height}};
    beginRendering(cmd_, &bloom_[0], nullptr, nullptr, clear, true, 1.0f, false, r0);
    VkViewport vp0{0.0f, (float)r0.extent.height, (float)r0.extent.width, -(float)r0.extent.height,
                   0.0f, 1.0f};
    vkCmdSetViewport(cmd_, 0, 1, &vp0);
    vkCmdSetScissor(cmd_, 0, 1, &r0);
    float push[4] = {1.0f, 0.0f, 0.0f, 0.0f};
    fullscreenDraw(cmd_, brightPipe_, setBright_, push, sizeof(push));
    vkCmdEndRendering(cmd_);
    transition(cmd_, bloom_[0], VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL);

    // Downsample chain.
    for (int i = 1; i < cfg_.bloomLevels; ++i) {
      Image& dst = bloom_[(size_t)i];
      transition(cmd_, dst, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);
      VkRect2D r{{0, 0}, {dst.width, dst.height}};
      beginRendering(cmd_, &dst, nullptr, nullptr, clear, true, 1.0f, false, r);
      VkViewport vp{0.0f, (float)r.extent.height, (float)r.extent.width, -(float)r.extent.height, 0.0f,
                    1.0f};
      vkCmdSetViewport(cmd_, 0, 1, &vp);
      vkCmdSetScissor(cmd_, 0, 1, &r);
      float p[4] = {(float)i, 0.0f, 0.0f, 0.0f};
      fullscreenDraw(cmd_, downPipe_, setDown_[(size_t)i], p, sizeof(p));
      vkCmdEndRendering(cmd_);
      transition(cmd_, dst, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL);
    }

  }

  // ------------------------------------------------------------- composite --
  if (!cfg_.bloom) transition(cmd_, bloom_[0], VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL);
  const bool useFxaa = cfg_.fxaa;
  Image& compositeTarget = useFxaa ? ldrColor_ : finalColor_;
  transition(cmd_, compositeTarget, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);
  beginRendering(cmd_, &compositeTarget, nullptr, nullptr, clear, false, 1.0f, false, fullRect);
  vkCmdSetViewport(cmd_, 0, 1, &sceneViewport);
  vkCmdSetScissor(cmd_, 0, 1, &fullRect);
  {
    float p[4] = {debugView, 0.0f, 0.0f, 0.0f};
    fullscreenDraw(cmd_, compositePipe_, setComposite_, p, sizeof(p));
  }
  vkCmdEndRendering(cmd_);
  if (useFxaa) transition(cmd_, ldrColor_, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL);

  // ------------------------------------------------------------------ FXAA --
  if (useFxaa) {
    transition(cmd_, finalColor_, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);
    beginRendering(cmd_, &finalColor_, nullptr, nullptr, clear, false, 1.0f, false, fullRect);
    vkCmdSetViewport(cmd_, 0, 1, &sceneViewport);
    vkCmdSetScissor(cmd_, 0, 1, &fullRect);
    float p[4] = {1.0f / (float)cfg_.width, 1.0f / (float)cfg_.height, 0.0f, 0.0f};
    fullscreenDraw(cmd_, fxaaPipe_, setFxaa_, p, sizeof(p));
    vkCmdEndRendering(cmd_);
  }

  // ---------------------------------------------------------------- readback -
  transition(cmd_, finalColor_, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL);
  VkBufferImageCopy region{};
  region.bufferOffset = 0;
  region.bufferRowLength = 0;
  region.bufferImageHeight = 0;
  region.imageSubresource = {VK_IMAGE_ASPECT_COLOR_BIT, 0, 0, 1};
  region.imageOffset = {0, 0, 0};
  region.imageExtent = {(uint32_t)cfg_.width, (uint32_t)cfg_.height, 1};
  vkCmdCopyImageToBuffer(cmd_, finalColor_.image, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL,
                         readbackBuf_.buffer, 1, &region);

  VKOK(vkEndCommandBuffer(cmd_));
  VkSubmitInfo si{VK_STRUCTURE_TYPE_SUBMIT_INFO};
  si.commandBufferCount = 1;
  si.pCommandBuffers = &cmd_;
  VKOK(vkQueueSubmit(queue_, 1, &si, fence_));
  VKOK(vkWaitForFences(device_, 1, &fence_, VK_TRUE, UINT64_MAX));
  VKOK(vkResetFences(device_, 1, &fence_));

  std::memcpy(readbackPixels_.data(), readbackBuf_.mapped, readbackPixels_.size());
  auto t1 = std::chrono::high_resolution_clock::now();
  lastRenderMs_ = std::chrono::duration<double, std::milli>(t1 - t0).count();
  return true;
}

}  // namespace hv
