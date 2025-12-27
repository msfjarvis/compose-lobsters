#! /usr/bin/env nix-shell
#! nix-shell -i bash -p imagemagick libwebp
# shellcheck shell=bash

set -euo pipefail

# Get the absolute path to the script file
SCRIPT_DIR="$(cd "$(dirname "$(readlink -f "${BASH_SOURCE[0]}")")" && pwd)"
SCREENSHOT_DIR="${SCRIPT_DIR}"/../screenshots

function run_maestro_flow() {
  local MAESTRO_FLOW="${1:?}"

  # Create a directory for generated screenshots since Maestro puts them in $PWD
  mkdir -p "${SCREENSHOT_DIR}"
  pushd "${SCREENSHOT_DIR}" || exit 1
  # Delete any existing screenshots
  rm -rf ./*.png

  # Allow demo mode to be enabled
  adb shell settings put global sysui_demo_allowed 1

  # Enable Demo Mode on the device with the following settings
  # - Hide notification icons
  # - Set clock to 1200 hrs
  adb shell am broadcast -a com.android.systemui.demo \
    -e command enter \
    -e command notifications -e visible false \
    -e command clock -e hhmm 1200

  # Clear data for the app to make re-runs consistent
  adb shell pm clear dev.msfjarvis.claw.android

  # Run the Maestro Flows from $PROJECT_DIR/maestro
  maestro test "${SCRIPT_DIR}"/../maestro/"${MAESTRO_FLOW}"

  # Turn off Demo Mode on the device
  adb shell am broadcast -a com.android.systemui.demo -e command exit

  # Return to the original $PWD
  popd || exit 1
}

function generate_grid() {
  local MONTAGE_INPUT BACKGROUND_COLOR OUTPUT_FILE START_NUMBER
  MONTAGE_INPUT="$(mktemp)"
  OUTPUT_FILE="${1:?}"
  BACKGROUND_COLOR="${2:?}"
  START_NUMBER="${3:?}"
  # Run the Maestro flow to generate the screenshots
  run_maestro_flow feature_graphic.yml

  # Fill out the input files with padding
  printf "%s\n%s\n%s\n%s\n%s\n%s\n" \
    "${SCREENSHOT_DIR}"/HottestPosts.png \
    "${SCREENSHOT_DIR}"/CommentsPage.png \
    "${SCREENSHOT_DIR}"/SavedPosts.png \
    "${SCREENSHOT_DIR}"/SearchPage.png \
    "xc:${BACKGROUND_COLOR}" \
    "${SCREENSHOT_DIR}"/SettingsPage.png \
    >> "${MONTAGE_INPUT}"

  # Use imagemagick to stitch the screenshots in a grid
  # Dimensions are of my Pixel 4a which I use for this purpose
  montage @"${MONTAGE_INPUT}" -tile 3x0 -geometry 1080x2340+0.0! \
    -border 0 -density 300 \
    "${OUTPUT_FILE}.png" || true

  # Convert the resulting PNG to WebP
  cwebp "${OUTPUT_FILE}.png" -o "${OUTPUT_FILE}.webp"

  # Remove the now-useless PNG and montage input text
  rm "${OUTPUT_FILE}.png" "${MONTAGE_INPUT}"

  local FASTLANE_SCREENSHOTS="${SCRIPT_DIR}"/../fastlane/metadata/android/en-US/images/phoneScreenshots
  local COUNTER="${START_NUMBER}"
  for screenshot in HottestPosts CommentsPage SavedPosts SearchPage SettingsPage; do
    if [[ -f "${SCREENSHOT_DIR}/${screenshot}.png" ]]; then
      cp "${SCREENSHOT_DIR}/${screenshot}.png" "${FASTLANE_SCREENSHOTS}/$(printf '%02d' "${COUNTER}").png"
      ((COUNTER++))
    fi
  done
}

adb shell "cmd uimode night no"

generate_grid "${SCRIPT_DIR}"/../.github/readme_feature_light white 1

adb shell "cmd uimode night yes"

generate_grid "${SCRIPT_DIR}"/../.github/readme_feature_dark black 6
