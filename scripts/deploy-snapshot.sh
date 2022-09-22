#!/usr/bin/env bash

set -ex

LATEST_TAG="latest"
CURRENT_REV="$(git rev-parse --short HEAD)"
ASSET_DIRECTORY="${GITHUB_WORKSPACE:?}/android/outputs"

function overwrite_local_tag() {
  git tag -f "${LATEST_TAG}"
}

function overwrite_remote_tag() {
  git push -f origin "${LATEST_TAG}"
}

function has_release() {
  gh release view "${LATEST_TAG}" &>/dev/null
  echo "$?"
}

function delete_release() {
  gh release delete --yes "${LATEST_TAG}"
}

function create_release() {
  local CHANGELOG_FILE
  CHANGELOG_FILE="$(mktemp)"
  echo "Latest release for Claw from revision ${CURRENT_REV}" | tee "${CHANGELOG_FILE}"
  pushd "${ASSET_DIRECTORY}" || return
  gh release create --prerelease --title "Latest snapshot build" --notes-file "${CHANGELOG_FILE}" "${LATEST_TAG}" ./*
  popd || return
}

overwrite_local_tag

if [[ "$(has_release)" -eq 0 ]]; then
  delete_release
fi

overwrite_remote_tag

create_release
