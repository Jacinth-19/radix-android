cmake_minimum_required(VERSION 3.16)
project(hair_vulkan LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)
if(NOT CMAKE_BUILD_TYPE)
  set(CMAKE_BUILD_TYPE Release)
endif()

# Root of the Vulkan SDK staged by tools/setup_vulkan_sdk.sh
# (vulkan headers + libvulkan loader).
set(VKB_SDK "$ENV{VKB_SDK}" CACHE PATH "Vulkan SDK root (include/ + lib/)")

find_path(VULKAN_INCLUDE_DIR vulkan/vulkan.h PATHS "${VKB_SDK}/include" NO_DEFAULT_PATH)
find_library(VULKAN_LIBRARY vulkan PATHS "${VKB_SDK}/lib" NO_DEFAULT_PATH)
if(NOT VULKAN_INCLUDE_DIR OR NOT VULKAN_LIBRARY)
  message(FATAL_ERROR "Vulkan SDK not found in '${VKB_SDK}'. Run tools/setup_vulkan_sdk.sh first.")
endif()

add_executable(hair_vulkan
  src/main.cpp
  src/hair.cpp
  src/scene.cpp
  src/vk_engine.cpp)

target_include_directories(hair_vulkan PRIVATE "${VULKAN_INCLUDE_DIR}" src)
target_link_libraries(hair_vulkan PRIVATE "${VULKAN_LIBRARY}")
target_compile_options(hair_vulkan PRIVATE -O2 -Wall -Wextra -Wno-unused-parameter -Wno-missing-field-initializers)
set_target_properties(hair_vulkan PROPERTIES BUILD_RPATH "${VKB_SDK}/lib")

message(STATUS "Vulkan headers: ${VULKAN_INCLUDE_DIR}")
message(STATUS "Vulkan loader : ${VULKAN_LIBRARY}")
