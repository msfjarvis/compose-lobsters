#! /usr/bin/env nix-shell
#! nix-shell -i bash -p imagemagick libwebp
# shellcheck shell=bash

set -euo pipefail

# Get the absolute path to the script file
SCRIPT_DIR="$(cd "$(dirname "$(readlink -f "${BASH_SOURCE[0]}")")" && pwd)"
SCREENSHOT_DIR="${SCRIPT_DIR}"/../screenshots

# Run the Maestro flow to generate the screenshots
"${SCRIPT_DIR}"/run-maestro-flows.sh feature_graphic.yml

# Use imagemagick to stitch the screenshots in a grid
montage -tile 2x0 -geometry +0+0 \
        -border 0 -density 300 \
        "${SCREENSHOT_DIR}"/HottestPosts.png \
        "${SCREENSHOT_DIR}"/CommentsPage.png \
        "${SCREENSHOT_DIR}"/SavedPosts.png \
        "${SCREENSHOT_DIR}"/SearchPage.png \
        "${SCRIPT_DIR}"/../.github/readme_feature.png

# Convert the resulting PNG to WebP
cwebp .github/readme_feature.png -o .github/readme_feature.webp

# Remove the now-useless PNG
rm ".github/readme_feature.png"
