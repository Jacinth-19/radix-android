#!/usr/bin/env bash
# Reproducible Linux x86_64 Android toolchain. No root permissions required.
set -euo pipefail
PROJECT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
TOOLS="${RADIX_TOOLCHAIN_ROOT:-$HOME/.cache/radix-toolchain}"
export ANDROID_USER_HOME="$TOOLS/android-user"
mkdir -p "$TOOLS/downloads" "$ANDROID_USER_HOME"
fetch() {
  if [ ! -f "$2" ]; then
    curl --fail --location --retry 3 --silent --show-error "$1" -o "$2.partial"
    mv "$2.partial" "$2"
  fi
}
install_jdk() {
  if [ ! -x "$TOOLS/jdk17/bin/javac" ]; then
    echo 'Downloading JDK 17 (Amazon Corretto)…'
    fetch 'https://corretto.aws/downloads/resources/17.0.20.12.1/amazon-corretto-17.0.20.12.1-linux-x64.tar.gz' "$TOOLS/downloads/jdk17.tar.gz"
    mkdir -p "$TOOLS/jdk17"
    tar -xzf "$TOOLS/downloads/jdk17.tar.gz" -C "$TOOLS/jdk17" --strip-components=1
  fi
}
install_cli() {
  if [ ! -x "$TOOLS/android-sdk/cmdline-tools/latest/bin/sdkmanager" ]; then
    echo 'Downloading Android command-line tools…'
    fetch 'https://dl.google.com/android/repository/commandlinetools-linux-16111833_latest.zip' "$TOOLS/downloads/commandline-tools.zip"
    echo "e025545c62a8e64c7559119566a569fb1dec5f60  $TOOLS/downloads/commandline-tools.zip" | sha1sum -c -
    mkdir -p "$TOOLS/android-sdk/cmdline-tools"
    unzip -q -o "$TOOLS/downloads/commandline-tools.zip" -d "$TOOLS/android-sdk/cmdline-tools"
    mv "$TOOLS/android-sdk/cmdline-tools/cmdline-tools" "$TOOLS/android-sdk/cmdline-tools/latest"
  fi
}
install_gradle() {
  if [ ! -x "$TOOLS/gradle-8.13/bin/gradle" ]; then
    echo 'Downloading Gradle 8.13…'
    fetch 'https://downloads.gradle.org/distributions/gradle-8.13-bin.zip' "$TOOLS/downloads/gradle.zip"
    fetch 'https://downloads.gradle.org/distributions/gradle-8.13-bin.zip.sha256' "$TOOLS/downloads/gradle.sha256"
    echo "$(cat "$TOOLS/downloads/gradle.sha256")  $TOOLS/downloads/gradle.zip" | sha256sum -c -
    unzip -q -o "$TOOLS/downloads/gradle.zip" -d "$TOOLS"
  fi
}
install_jdk & JDK_PID=$!
install_cli & CLI_PID=$!
install_gradle & GRADLE_PID=$!
wait "$JDK_PID"; wait "$CLI_PID"; wait "$GRADLE_PID"
export JAVA_HOME="$TOOLS/jdk17"
export ANDROID_HOME="$TOOLS/android-sdk"
export ANDROID_SDK_ROOT="$ANDROID_HOME"
export GRADLE_USER_HOME="$HOME/.cache/radix-gradle"
export PATH="$JAVA_HOME/bin:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$TOOLS/gradle-8.13/bin:$PATH"
# This setup script accepts Google's Android SDK licenses. Review them before running:
# https://developer.android.com/studio/terms
set +o pipefail
yes | sdkmanager --sdk_root="$ANDROID_HOME" --licenses > "$TOOLS/sdk-licenses.log" 2>&1
LICENSE_STATUS=${PIPESTATUS[1]}
set -o pipefail
[ "$LICENSE_STATUS" -eq 0 ] || { cat "$TOOLS/sdk-licenses.log"; exit "$LICENSE_STATUS"; }
sdkmanager --sdk_root="$ANDROID_HOME" 'platform-tools' 'platforms;android-36' 'build-tools;36.0.0'
printf 'sdk.dir=%s\n' "$ANDROID_HOME" > "$PROJECT/local.properties"
cat > "$TOOLS/env.sh" <<EOF
export JAVA_HOME="$JAVA_HOME"
export ANDROID_HOME="$ANDROID_HOME"
export ANDROID_SDK_ROOT="$ANDROID_HOME"
export ANDROID_USER_HOME="$ANDROID_USER_HOME"
export GRADLE_USER_HOME="$GRADLE_USER_HOME"
export PATH="$JAVA_HOME/bin:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$TOOLS/gradle-8.13/bin:\$PATH"
EOF
printf '\nToolchain ready. Run:\n  source "%s/env.sh"\n  cd "%s"\n  ./gradlew testDebugUnitTest assembleDebug\n' "$TOOLS" "$PROJECT"
java -version
adb version
