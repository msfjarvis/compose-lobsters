#!/usr/bin/env bash

set -euo pipefail

manifest_name="manifest.zipline.json"

usage() {
  cat <<'EOF'
Usage:
  deploy-zipline-parser.sh release <source-dir> <target-dir> [timestamp]
  deploy-zipline-parser.sh rollback <target-dir> <timestamp>
  deploy-zipline-parser.sh list <target-dir>
  deploy-zipline-parser.sh status <target-dir>
  deploy-zipline-parser.sh prune <target-dir>

Commands:
  release   Create a timestamped release in <target-dir>/releases/<timestamp>
            and atomically repoint <target-dir>/current to it.
  rollback  Repoint <target-dir>/current to an existing release timestamp.
  list      List available release timestamps.
  status    Print the current active release.
  prune     Delete all but the 5 most recent releases. Never deletes the active release.
EOF
}

err() {
  echo "error: $*" >&2
  exit 1
}

timestamp_now() {
  date -u +%Y%m%dT%H%M%SZ
}

require_dir() {
  local dir="$1"
  [[ -d "${dir}" ]] || err "directory does not exist: ${dir}"
}

release_command() {
  local src_dir="$1"
  local target_dir="$2"
  local timestamp="${3:-$(timestamp_now)}"
  local manifest_path="${src_dir}/${manifest_name}"
  local releases_dir="${target_dir}/releases"
  local release_dir="${releases_dir}/${timestamp}"
  local tmp_link

  require_dir "${src_dir}"
  [[ -f "${manifest_path}" ]] || err "missing manifest: ${manifest_path}"

  shopt -s nullglob
  local module_files=("${src_dir}"/*.zipline)
  shopt -u nullglob
  [[ "${#module_files[@]}" -gt 0 ]] || err "no .zipline module files found in ${src_dir}"
  [[ ! -e "${release_dir}" ]] || err "release already exists: ${release_dir}"

  mkdir -p "${releases_dir}"
  mkdir -p "${release_dir}"

  echo "Creating release ${timestamp} in ${release_dir}"
  for module_path in "${module_files[@]}"; do
    local module_name
    module_name="$(basename "${module_path}")"
    echo "Copying module: ${module_name}"
    cp "${module_path}" "${release_dir}/${module_name}"
  done

  echo "Copying manifest last: ${manifest_name}"
  cp "${manifest_path}" "${release_dir}/${manifest_name}"

  tmp_link="${target_dir}/.current.${timestamp}.tmp"
  ln -sfn "${release_dir}" "${tmp_link}"
  mv -Tf "${tmp_link}" "${target_dir}/current"

  echo "Activated release ${timestamp}"
}

rollback_command() {
  local target_dir="$1"
  local timestamp="$2"
  local release_dir="${target_dir}/releases/${timestamp}"
  local tmp_link="${target_dir}/.current.${timestamp}.tmp"

  require_dir "${target_dir}"
  [[ -d "${release_dir}" ]] || err "release does not exist: ${release_dir}"
  [[ -f "${release_dir}/${manifest_name}" ]] || err "release is missing manifest: ${release_dir}/${manifest_name}"

  ln -sfn "${release_dir}" "${tmp_link}"
  mv -Tf "${tmp_link}" "${target_dir}/current"

  echo "Rolled back current release to ${timestamp}"
}

list_command() {
  local target_dir="$1"
  local releases_dir="${target_dir}/releases"

  require_dir "${target_dir}"
  mkdir -p "${releases_dir}"
  find "${releases_dir}" -mindepth 1 -maxdepth 1 -type d -exec basename {} \; | sort
}

status_command() {
  local target_dir="$1"
  local current_link="${target_dir}/current"

  require_dir "${target_dir}"
  [[ -L "${current_link}" ]] || err "current release symlink is missing: ${current_link}"

  local current_target
  current_target="$(readlink "${current_link}")"
  local current_timestamp
  current_timestamp="$(basename "${current_target}")"

  echo "current=${current_timestamp}"
  echo "path=${current_target}"
}

prune_command() {
  local target_dir="$1"
  local releases_dir="${target_dir}/releases"
  local current_link="${target_dir}/current"

  require_dir "${target_dir}"
  mkdir -p "${releases_dir}"

  local current_timestamp=""
  if [[ -L "${current_link}" ]]; then
    current_timestamp="$(basename "$(readlink "${current_link}")")"
  fi

  mapfile -t releases < <(find "${releases_dir}" -mindepth 1 -maxdepth 1 -type d -exec basename {} \; | sort)

  if [[ "${#releases[@]}" -le 5 ]]; then
    echo "Nothing to prune"
    return 0
  fi

  local delete_count=$((${#releases[@]} - 5))
  for ((i=0; i<delete_count; i++)); do
    local timestamp="${releases[$i]}"
    if [[ -n "${current_timestamp}" && "${timestamp}" == "${current_timestamp}" ]]; then
      echo "Skipping active release: ${timestamp}"
      continue
    fi
    echo "Pruning release: ${timestamp}"
    rm -rf "${releases_dir}/${timestamp}"
  done
}

main() {
  local command="${1:-}"
  case "${command}" in
    release)
      [[ "$#" -eq 3 || "$#" -eq 4 ]] || { usage >&2; exit 1; }
      release_command "$2" "$3" "${4:-}"
      ;;
    rollback)
      [[ "$#" -eq 3 ]] || { usage >&2; exit 1; }
      rollback_command "$2" "$3"
      ;;
    list)
      [[ "$#" -eq 2 ]] || { usage >&2; exit 1; }
      list_command "$2"
      ;;
    status)
      [[ "$#" -eq 2 ]] || { usage >&2; exit 1; }
      status_command "$2"
      ;;
    prune)
      [[ "$#" -eq 2 ]] || { usage >&2; exit 1; }
      prune_command "$2"
      ;;
    -h|--help)
      usage
      ;;
    *)
      usage >&2
      exit 1
      ;;
  esac
}

main "$@"
