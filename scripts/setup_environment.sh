#!/usr/bin/env bash

set -euxo pipefail

CMDLINE_TOOLS_URL_MAC="https://dl.google.com/android/repository/commandlinetools-mac-6858069_latest.zip"
CMDLINE_TOOLS_URL_LINUX="https://dl.google.com/android/repository/commandlinetools-linux-6858069_latest.zip"

if [ "$(uname)" == "Linux" ]; then
  wget "${CMDLINE_TOOLS_URL_LINUX}" -O /tmp/tools.zip -o /dev/null
elif [ "$(uname)" == "Darwin" ]; then
  wget "${CMDLINE_TOOLS_URL_MAC}" -O /tmp/tools.zip -o /dev/null
else
  echo "This script only works on Linux and Mac"
  exit 1
fi
unzip -qo /tmp/tools.zip -d "${ANDROID_SDK_ROOT}/latest"
mkdir -p "${ANDROID_SDK_ROOT}/cmdline-tools"
mv -v "${ANDROID_SDK_ROOT}/latest/cmdline-tools" "${ANDROID_SDK_ROOT}/cmdline-tools/latest"

export PATH="${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin:${PATH}"

sdkmanager --install 'build-tools;30.0.3' platform-tools 'platforms;android-30'
sdkmanager --install emulator
sdkmanager --install 'system-images;android-30;google_apis;x86'

