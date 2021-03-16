#!/usr/bin/env bash

# Creates and boots an emulator that exactly matches the one in our CI. It is recommended
# to use this as the target device for screenshot tests.

set -euxo pipefail

[ -n "${ANDROID_SDK_ROOT}" ] || { echo "ANDROID_SDK_ROOT must be set to use this script"; exit 1; }

echo no | "${ANDROID_SDK_ROOT}"/cmdline-tools/latest/bin/avdmanager create avd \
  --force \
  -n Pixel_XL_API_30 \
  --abi 'google_apis/x86' \
  --package 'system-images;android-30;google_apis;x86' \
  --device 'pixel_xl'

"${ANDROID_SDK_ROOT}"/emulator/emulator \
  -avd Pixel_XL_API_30 \
  -no-window \
  -gpu swiftshader_indirect \
  -no-snapshot \
  -noaudio \
  -no-boot-anim
