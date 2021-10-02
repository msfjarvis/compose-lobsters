#!/usr/bin/env bash

set -euo pipefail

export SSHDIR="$HOME/.ssh"
export SERVER_DEPLOY_STRING="$SSH_USERNAME@$SERVER_ADDRESS:$SERVER_DESTINATION"
mkdir -p "$SSHDIR"
echo "$ACTIONS_DEPLOY_KEY" > "$SSHDIR/key"
chmod 600 "$SSHDIR/key"
mkdir -p "$GITHUB_WORKSPACE/Claw"
find . -type f -name '*.apk' -exec cp {} "$GITHUB_WORKSPACE/Claw/Claw.apk" \;
find . -type f -name '*.deb' -exec cp {} "$GITHUB_WORKSPACE/Claw/" \;
cd "$GITHUB_WORKSPACE/Claw"
rsync -ahvcr --omit-dir-times --progress --delete --no-o --no-g -e "ssh -i $SSHDIR/key -o StrictHostKeyChecking=no -p $SSH_PORT" . "$SERVER_DEPLOY_STRING"
