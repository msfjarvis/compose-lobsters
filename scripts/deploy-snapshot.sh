#!/usr/bin/env bash

set -euo pipefail

export SSHDIR="$HOME/.ssh"
export SERVER_DEPLOY_STRING="$SSH_USERNAME@$SERVER_ADDRESS:$SERVER_DESTINATION"
export VERSION_NAME="$(cat app/version.properties | grep versionName | cut -d '=' -f 2)"
mkdir -p "$SSHDIR"
echo "$ACTIONS_DEPLOY_KEY" > "$SSHDIR/key"
chmod 600 "$SSHDIR/key"
mkdir -p "$GITHUB_WORKSPACE/Claw"
cp -v ./app/build/outputs/apk/release/app-release.apk "$GITHUB_WORKSPACE/Claw/Claw-${VERSION_NAME}.apk"
cd "$GITHUB_WORKSPACE/Claw"
rsync -ahvcr --omit-dir-times --progress --delete --no-o --no-g -e "ssh -i $SSHDIR/key -o StrictHostKeyChecking=no -p $SSH_PORT" . "$SERVER_DEPLOY_STRING"
