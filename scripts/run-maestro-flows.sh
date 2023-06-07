#!/usr/bin/env bash

set -euo pipefail

# Get the absolute path to the script file
SCRIPT_DIR="$(cd "$(dirname "$(readlink -f "${BASH_SOURCE[0]}")")" && pwd)"

MAESTRO_FLOW="${1:-}"
if [[ -z "${MAESTRO_FLOW}" ]]; then
  echo "No flow file was provided"
  echo "Usage: run-maestro-flows.sh flow.yml"
  exit 1
elif [[ ! -f "${SCRIPT_DIR}"/../maestro/"${MAESTRO_FLOW}" ]]; then
  echo "Could not find flow ${MAESTRO_FLOW}"
  echo "Make sure the flow file is in the maestro directory"
  exit 1
fi

# Create a directory for generated screenshots since Maestro puts them iN $PWD
mkdir -p "${SCRIPT_DIR}"/../screenshots
pushd "${SCRIPT_DIR}"/../screenshots || exit 1
# Delete any existing screenshots
rm -rf ./*.png

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
