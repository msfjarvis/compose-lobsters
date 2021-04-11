#!/usr/bin/env bash

set -euo pipefail

# Delete Release key
rm -f keystore.jks

# Delete signing config
rm -f keystore.properties
