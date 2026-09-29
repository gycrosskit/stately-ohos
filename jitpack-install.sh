#!/usr/bin/env bash
set -euo pipefail

archive=stately-maven.tar.gz
curl -fL --retry 3 -o "$archive" "https://github.com/gycrosskit/stately-ohos/releases/download/${VERSION}/${archive}?download=1"
echo "db07fb9082619fd9d407813a4957969a4ead0b90c87625e8636668f58f6e7816  $archive" | sha256sum -c -
mkdir -p "$HOME/.m2/repository"
tar -xzf "$archive" -C "$HOME/.m2/repository"
