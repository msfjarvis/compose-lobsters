#!/usr/bin/env bash

# Creates and boots an emulator that exactly matches the one in our CI. It is recommended
# to use this as the target device for screenshot tests.

set -euo pipefail

[ -n "${ANDROID_HOME:-}" ] || {
  echo "ANDROID_HOME must be set to use this script"
  exit 1
}
[ -n "${ANDROID_AVD_HOME:-}" ] || {
  export ANDROID_AVD_HOME="${ANDROID_HOME}"
}
[ -n "${ANDROID_API_LEVEL:-}" ] || { echo "ANDROID_API_LEVEL not defined; defaulting to 33"; }

ARCH="x86_64"
if [[ "$(uname -m)" == "arm64" && "$(uname)" == "Darwin" ]]; then
  ARCH="arm64-v8a"
fi

API_LEVEL="${ANDROID_API_LEVEL:-33}"

sdkmanager "system-images;android-${API_LEVEL};google_apis;${ARCH}"

"${ANDROID_HOME}"/cmdline-tools/latest/bin/avdmanager create avd \
  --force \
  -n "Pixel_XL_API_${API_LEVEL}" \
  --abi "google_apis/${ARCH}" \
  --package "system-images;android-${API_LEVEL};google_apis;${ARCH}" \
  --device 'pixel_xl'

"${ANDROID_HOME}"/emulator/emulator \
  -avd "Pixel_XL_API_${API_LEVEL}" \
  -gpu 'swiftshader_indirect' \
  -noaudio
