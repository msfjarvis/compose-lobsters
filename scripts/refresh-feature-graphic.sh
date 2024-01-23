#! /usr/bin/env nix-shell
#! nix-shell -i bash -p imagemagick libwebp
# shellcheck shell=bash

set -euo pipefail

# Get the absolute path to the script file
SCRIPT_DIR="$(cd "$(dirname "$(readlink -f "${BASH_SOURCE[0]}")")" && pwd)"
SCREENSHOT_DIR="${SCRIPT_DIR}"/../screenshots

function generate_grid() {
  local MONTAGE_INPUT BACKGROUND_COLOR OUTPUT_FILE
  MONTAGE_INPUT="$(mktemp)"
  OUTPUT_FILE="${1:?}"
  BACKGROUND_COLOR="${2:?}"
  # Run the Maestro flow to generate the screenshots
  "${SCRIPT_DIR}"/run-maestro-flows.sh feature_graphic.yml

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
}

adb shell "cmd uimode night no"

generate_grid "${SCRIPT_DIR}"/../.github/readme_feature_light white

adb shell "cmd uimode night yes"

generate_grid "${SCRIPT_DIR}"/../.github/readme_feature_dark black
