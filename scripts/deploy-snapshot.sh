#!/usr/bin/env bash

set -euo pipefail

export SSHDIR="$HOME/.ssh"
mkdir -p "$SSHDIR"
echo "$ACTIONS_DEPLOY_KEY" > "$SSHDIR/key"
chmod 600 "$SSHDIR/key"
export SERVER_DEPLOY_STRING="$SSH_USERNAME@$SERVER_ADDRESS:$SERVER_DESTINATION"
mkdir -p "$GITHUB_WORKSPACE/Claw"
cp -v ./app/build/outputs/apk/release/*.apk "$GITHUB_WORKSPACE/Claw/"
cd "$GITHUB_WORKSPACE/Claw"
rsync -ahvcr --omit-dir-times --progress --delete --no-o --no-g -e "ssh -i $SSHDIR/key -o StrictHostKeyChecking=no -p $SSH_PORT" . "$SERVER_DEPLOY_STRING"
