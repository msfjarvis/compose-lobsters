#!/usr/bin/env bash

set -euxo pipefail

gradle -q clearPreRelease

VERSION="$(tail -n1 android/version.properties | cut -d = -f 2)"

git commit -am 'feat(release): bump version'

gradle -q collectReleaseApks collectReleaseBundle

gradle -q bumpSnapshot

git commit -am 'feat(release): start next development iteration'

gh release create "v${VERSION}" ./android/outputs/*
