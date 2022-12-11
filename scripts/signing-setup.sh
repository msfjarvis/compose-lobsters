#!/usr/bin/env bash

set -euo pipefail

ENCRYPT_KEY="${1}"
TEMP_KEY="$(mktemp)"

trap "rm -rf ${TEMP_KEY} 2>/dev/null" INT TERM EXIT

echo "${ENCRYPT_KEY:?}" > "${TEMP_KEY}"

function decrypt() {
  if ! command -v age 1>/dev/null; then
    echo "age not installed"
    exit 1
  fi
  SRC="${1}"
  DST="${2}"
  age --decrypt -i "${TEMP_KEY}" -o "${DST:?}" "${SRC:?}"
}

decrypt secrets/keystore.cipher keystore.jks
decrypt secrets/props.cipher keystore.properties
