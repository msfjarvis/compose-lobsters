#!/usr/bin/env bash

set -euo pipefail

ENCRYPT_KEY="${1}"
KEY_FILE="$(mktemp)"

trap "rm -rf ${KEY_FILE} 2>/dev/null" INT TERM EXIT

echo "${ENCRYPT_KEY:?}" > "${KEY_FILE}"

function decrypt() {
  if ! command -v age 1>/dev/null; then
    echo "age not installed"
    exit 1
  fi
  SRC="${1}"
  DST="${2}"
  age --decrypt -i "${KEY_FILE}" -o "${DST:?}" "${SRC:?}"
}

decrypt secrets/keystore.jks.age keystore.jks
decrypt secrets/keystore.properties.age keystore.properties
