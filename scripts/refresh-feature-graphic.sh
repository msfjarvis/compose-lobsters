#! /usr/bin/env nix-shell
#! nix-shell -i bash -p imagemagick libwebp
# shellcheck shell=bash

set -euo pipefail

# Get the absolute path to the script file
SCRIPT_DIR="$(cd "$(dirname "$(readlink -f "${BASH_SOURCE[0]}")")" && pwd)"
SCREENSHOT_DIR="${SCRIPT_DIR}"/../screenshots

function generate_grid() {
  # Run the Maestro flow to generate the screenshots
  "${SCRIPT_DIR}"/run-maestro-flows.sh feature_graphic.yml

  # Use imagemagick to stitch the screenshots in a grid
  montage -tile 2x0 -geometry +0+0 \
    -border 0 -density 300 \
    "${SCREENSHOT_DIR}"/HottestPosts.png \
    "${SCREENSHOT_DIR}"/CommentsPage.png \
    "${SCREENSHOT_DIR}"/SavedPosts.png \
    "${SCREENSHOT_DIR}"/SearchPage.png \
    "${1:?}.png" || true

  # Convert the resulting PNG to WebP
  cwebp "${1:?}.png" -o "${1:?}.webp"

  # Remove the now-useless PNG
  rm "${1:?}.png"
}

adb shell "cmd uimode night no"

generate_grid "${SCRIPT_DIR}"/../.github/readme_feature_light

adb shell "cmd uimode night yes"

generate_grid "${SCRIPT_DIR}"/../.github/readme_feature_dark
