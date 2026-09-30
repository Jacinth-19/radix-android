#!/usr/bin/env bash
# Sign an already-built release APK. The key and credentials stay outside the source tree.
set -euo pipefail
if [[ $# -lt 2 || $# -gt 3 ]]; then
  printf 'Usage: %s /private/key.p12 /private/password-file [output.apk]\n' "$0" >&2
  exit 2
fi
root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
key="$(realpath "$1")"
password="$(realpath "$2")"
output="${3:-$root/Radix-release.apk}"
sdk="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}"
if [[ -z "$sdk" ]]; then printf 'Set ANDROID_HOME or source the toolchain env.sh first.\n' >&2; exit 2; fi
build_tools="$sdk/build-tools/36.0.0"
input="$root/app/build/outputs/apk/release/app-release-unsigned.apk"
if [[ ! -f "$input" ]]; then printf 'Build first: ./gradlew assembleRelease\n' >&2; exit 2; fi
if [[ ! -f "$key" || ! -r "$password" ]]; then printf 'Missing private key or password file.\n' >&2; exit 2; fi
if [[ "$key" == "$root/"* || "$password" == "$root/"* ]]; then
  printf 'Keep the key and password outside the distributed source tree.\n' >&2; exit 2
fi
mkdir -p "$(dirname "$output")"
aligned="$(mktemp "${output}.aligned.XXXXXX")"
trap 'rm -f "$aligned"' EXIT
"$build_tools/zipalign" -P 16 -f 4 "$input" "$aligned"
# The PKCS12 entry uses the store password; reusing one password-file source
# for both --ks-pass and --key-pass would consume its single line twice.
"$build_tools/apksigner" sign --ks "$key" --ks-key-alias "${RADIX_KEY_ALIAS:-radix-release}" --ks-pass "file:$password" --v1-signing-enabled true --v2-signing-enabled true --v3-signing-enabled true --v4-signing-enabled false --out "$output" "$aligned"
"$build_tools/zipalign" -P 16 -c 4 "$output"
"$build_tools/apksigner" verify --verbose --print-certs "$output"
printf '\nVerified signed release: %s\n' "$output"
