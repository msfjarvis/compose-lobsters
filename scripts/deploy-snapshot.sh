#!/usr/bin/env bash

set -euxo pipefail

NIGHTLY_TAG="latest"
CURRENT_REV="$(git rev-parse --short HEAD)"
ASSET_DIRECTORY="${GITHUB_WORKSPACE:?}/android/apk"

function overwrite_local_tag() {
  git tag -f "${NIGHTLY_TAG}"
}

function overwrite_remote_tag() {
  git push -f origin "${NIGHTLY_TAG}"
}

function has_release() {
  gh release view "${NIGHTLY_TAG}" &> /dev/null
  echo "$?"
}

function delete_release() {
  gh release delete --yes "${NIGHTLY_TAG}"
}

function create_release() {
  local CHANGELOG_FILE
  CHANGELOG_FILE="$(mktemp)"
  echo "Latest release for Claw from revision ${CURRENT_REV}" | tee "${CHANGELOG_FILE}"
  pushd "${ASSET_DIRECTORY}" || return
  gh release create --prerelease --title "Latest snapshot build" --notes-file "${CHANGELOG_FILE}" "${NIGHTLY_TAG}" ./*.apk
  popd || return
}

overwrite_local_tag

if [[ "$(has_release)" -eq 0 ]]; then
  delete_release
fi

overwrite_remote_tag

create_release
