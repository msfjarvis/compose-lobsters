#!/usr/bin/env bash

set -euo pipefail

readonly archive_name="zipline-release.zip"
readonly production_dir="zipline-parser/build/zipline/Production"
readonly remote_host="msfjarvis@wailord"
readonly remote_deploy_script_rel="git-repos/compose-lobsters/scripts/deploy-zipline-parser.sh"
readonly remote_target_dir="/var/lib/claw-deploy/"
readonly gradle_tasks=(
  :zipline-parser:jsTest
  :zipline-parser:compileProductionExecutableKotlinJsZipline
)

usage() {
  cat <<'EOF'
Usage:
  scripts/release-zipline-parser.sh

Build the production Zipline parser bundle, archive it, upload it to wailord,
and activate it using the deploy script on the server.

Required environment:
  ZIPLINE_SIGNING_KEY   Signing key used during the production Zipline build.
EOF
}

err() {
  echo "error: $*" >&2
  exit 1
}

require_command() {
  local command="$1"
  command -v "${command}" >/dev/null 2>&1 || err "missing required command: ${command}"
}

main() {
  [[ "${1:-}" =~ ^(-h|--help)$ ]] && { usage; exit 0; }
  [[ "$#" -eq 0 ]] || { usage >&2; exit 1; }

  [[ -n "${ZIPLINE_SIGNING_KEY:-}" ]] || err "ZIPLINE_SIGNING_KEY must be set"

  require_command build-brief
  require_command zip
  require_command rsync
  require_command ssh
  require_command unzip

  echo "Building production Zipline bundle"
  build-brief ./gradlew "${gradle_tasks[@]}"

  [[ -d "${production_dir}" ]] || err "missing production output directory: ${production_dir}"

  local archive_path
  archive_path="$(mktemp -t zipline-release.XXXXXX).zip"
  trap "rm -f -- ${archive_path@Q}" EXIT

  echo "Creating release archive: ${archive_path}"
  (
    cd "${production_dir}"
    zip -r -j "${archive_path}" .
  )

  local remote_archive_name
  remote_archive_name="$(basename "${archive_path}")"
  echo "Uploading archive to ${remote_host}:~/${remote_archive_name}"
  rsync "${archive_path}" "${remote_host}:~/${remote_archive_name}"

  echo "Deploying archive on ${remote_host}"
  ssh "${remote_host}" "ARCHIVE_NAME=${remote_archive_name@Q} DEPLOY_SCRIPT_REL=${remote_deploy_script_rel@Q} TARGET_DIR=${remote_target_dir@Q} bash -s" <<'EOF'
set -euo pipefail

archive_path="${HOME}/${ARCHIVE_NAME}"
deploy_script="${HOME}/${DEPLOY_SCRIPT_REL}"
release_dir="$(mktemp -d)"
trap 'rm -rf "${release_dir}" "${archive_path}"' EXIT

unzip -d "${release_dir}" "${archive_path}"
"${deploy_script}" release "${release_dir}" "${TARGET_DIR}"
EOF

  echo "Zipline parser release deployed successfully"
}

main "$@"
