// Minimal dependency-free PNG writer (8-bit RGB, stored/uncompressed DEFLATE).
// Writing the PNG output does not require zlib, libpng or any image library.
#pragma once

#include <cstdint>
#include <cstdio>
#include <cstring>
#include <string>
#include <vector>

namespace hv {

namespace pngdetail {

inline uint32_t crc32(const uint8_t* data, size_t len, uint32_t crc = 0xffffffffu) {
  static uint32_t table[256];
  static bool init = false;
  if (!init) {
    for (uint32_t i = 0; i < 256; ++i) {
      uint32_t c = i;
      for (int k = 0; k < 8; ++k) c = (c & 1) ? (0xedb88320u ^ (c >> 1)) : (c >> 1);
      table[i] = c;
    }
    init = true;
  }
  for (size_t i = 0; i < len; ++i) crc = table[(crc ^ data[i]) & 0xff] ^ (crc >> 8);
  return crc;
}

inline void put32(std::vector<uint8_t>& v, uint32_t x) {
  v.push_back((uint8_t)(x >> 24));
  v.push_back((uint8_t)(x >> 16));
  v.push_back((uint8_t)(x >> 8));
  v.push_back((uint8_t)x);
}

inline void chunk(std::vector<uint8_t>& out, const char* type, const std::vector<uint8_t>& data) {
  put32(out, (uint32_t)data.size());
  std::vector<uint8_t> body(type, type + 4);
  body.insert(body.end(), data.begin(), data.end());
  out.insert(out.end(), body.begin(), body.end());
  put32(out, crc32(body.data(), body.size()) ^ 0xffffffffu);
}

}  // namespace pngdetail

// rgba: width*height*4 bytes, row stride = width*4 (top-down).
inline bool writePNG(const std::string& path, int width, int height, const uint8_t* rgba,
                     bool writeAlpha = false) {
  using namespace pngdetail;
  const int channels = writeAlpha ? 4 : 3;
  std::vector<uint8_t> raw;
  raw.reserve((size_t)height * (size_t)(width * channels + 1));
  for (int y = 0; y < height; ++y) {
    raw.push_back(0);  // filter: none
    const uint8_t* row = rgba + (size_t)y * (size_t)width * 4;
    for (int x = 0; x < width; ++x) {
      raw.push_back(row[x * 4 + 0]);
      raw.push_back(row[x * 4 + 1]);
      raw.push_back(row[x * 4 + 2]);
      if (writeAlpha) raw.push_back(row[x * 4 + 3]);
    }
  }

  // zlib stream with stored (uncompressed) DEFLATE blocks.
  std::vector<uint8_t> z;
  z.push_back(0x78);
  z.push_back(0x01);
  size_t offset = 0;
  const size_t kMaxBlock = 65535;
  while (offset < raw.size()) {
    size_t n = raw.size() - offset;
    if (n > kMaxBlock) n = kMaxBlock;
    bool final = (offset + n) >= raw.size();
    z.push_back(final ? 1 : 0);
    z.push_back((uint8_t)(n & 0xff));
    z.push_back((uint8_t)(n >> 8));
    z.push_back((uint8_t)(~n & 0xff));
    z.push_back((uint8_t)((~n >> 8) & 0xff));
    z.insert(z.end(), raw.begin() + offset, raw.begin() + offset + n);
    offset += n;
  }
  uint32_t a = 1, b = 0;
  for (uint8_t byte : raw) {
    a = (a + byte) % 65521;
    b = (b + a) % 65521;
  }
  put32(z, (b << 16) | a);

  std::vector<uint8_t> png = {0x89, 'P', 'N', 'G', 0x0d, 0x0a, 0x1a, 0x0a};
  std::vector<uint8_t> ihdr;
  put32(ihdr, (uint32_t)width);
  put32(ihdr, (uint32_t)height);
  ihdr.push_back(8);                    // bit depth
  ihdr.push_back(writeAlpha ? 6 : 2);   // color type: RGBA / RGB
  ihdr.push_back(0);                    // compression
  ihdr.push_back(0);                    // filter
  ihdr.push_back(0);                    // interlace
  chunk(png, "IHDR", ihdr);
  chunk(png, "IDAT", z);
  chunk(png, "IEND", {});

  FILE* f = std::fopen(path.c_str(), "wb");
  if (!f) return false;
  size_t written = std::fwrite(png.data(), 1, png.size(), f);
  std::fclose(f);
  return written == png.size();
}

}  // namespace hv
