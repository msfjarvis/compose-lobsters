#!/usr/bin/env bash

set -euxo pipefail

TEMP_DIR="$(mktemp -d)"
BIN_DIR=""
AGE_VERSION="v1.1.1"
AGE_FILE=""

case "$(uname)" in
  Linux)
    AGE_FILE="age-${AGE_VERSION}-linux-amd64.tar.gz"
    BIN_DIR="${HOME}/.local/bin"
    ;;
  Darwin)
    AGE_FILE="age-${AGE_VERSION}-darwin-amd64.tar.gz"
    BIN_DIR="${HOME}/bin"
    ;;
  *) echo "Unsupported system: $(uname)"; exit 1 ;;
esac

pushd "${TEMP_DIR}"

curl -L --silent --show-error --retry 3 --fail -o age.tar.gz "https://github.com/FiloSottile/age/releases/download/${AGE_VERSION}/${AGE_FILE:?}"
tar xvf age.tar.gz
rm age/LICENSE
mkdir -p "${BIN_DIR}"
mv -v age/age "${BIN_DIR}" && chmod +x "${BIN_DIR}/age"
mv -v age/age-keygen "${BIN_DIR}" && chmod +x "${BIN_DIR}/age-keygen"

popd
