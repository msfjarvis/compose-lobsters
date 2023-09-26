#!/usr/bin/env bash

set -euo pipefail

INPUT_FILE="${1:-}"
OUTPUT_FILE="${2:-}"
AGE_KEY="${3:-}"

if ! command -v age 1>/dev/null; then
  echo "age not installed"
  exit 1
fi

if [[ -n $AGE_KEY && -n $INPUT_FILE && -n $OUTPUT_FILE ]]; then
  age --encrypt -r "$(echo "${AGE_KEY}" | age-keygen -y)" -o "${OUTPUT_FILE}" <"${INPUT_FILE}"
else
  echo "Usage: ./encrypt-secret.sh <input file> <output file> <encryption key>"
fi
