#!/usr/bin/env bash
# Serial validation for RAM-limited hosts. Never combine Kotlin compilation,
# R8 and an out-of-process lint worker in one long-lived Gradle daemon.
set -euo pipefail
root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$root"
if [[ -f "$HOME/.cache/radix-toolchain/env.sh" ]]; then
  source "$HOME/.cache/radix-toolchain/env.sh"
fi
if [[ -z "${JAVA_HOME:-}" || -z "${ANDROID_HOME:-}" ]]; then
  printf 'Bootstrap/source the Android JDK17+SDK toolchain first.\n' >&2
  exit 2
fi
run() { printf '\n===== Native phase: %s =====\n' "$*"; ./gradlew --no-daemon --console=plain "$@"; }
run testDebugUnitTest
run testReleaseUnitTest
run assembleDebug
# Full lint is intentionally deferred to dedicated low-memory invocations below.
run assembleRelease -x lintVitalAnalyzeRelease -x lintVitalReportRelease -x lintVitalRelease
lint_jvm='-Xmx384m -XX:MaxMetaspaceSize=320m -XX:ActiveProcessorCount=2 -Dfile.encoding=UTF-8'
run "-Dorg.gradle.jvmargs=$lint_jvm" -Pandroid.experimental.lint.heapSize=512m lintDebug
run "-Dorg.gradle.jvmargs=$lint_jvm" -Pandroid.experimental.lint.heapSize=512m lintRelease
printf '\nAll native phases passed. APKs are not device-runtime tested by this script.\n'
