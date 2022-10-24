#!/usr/bin/env bash

set -euxo pipefail

gradle -q clearPreRelease

VERSION="$(tail -n1 android/version.properties | cut -d = -f 2)"

git commit -am 'feat(release): bump version'

gradle -q collectReleaseApks collectReleaseBundle

git tag -s "v${VERSION}" -F distribution/whatsnew/whatsnew-en-GB

gradle -q bumpSnapshot

truncate -s 0 distribution/whatsnew/whatsnew-en-GB

git commit -am 'feat(release): start next development iteration'

git push origin main "v${VERSION}"

gh release create "v${VERSION}" -F distribution/whatsnew/whatsnew-en-GB --title "v${VERSION}" ./android/apk/*.apk ./android/bundle/*.aab
