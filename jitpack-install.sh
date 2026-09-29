#!/usr/bin/env bash
set -euo pipefail

archive=stately-maven.tar.gz
curl -fL --retry 3 -o "$archive" "https://github.com/gycrosskit/stately-ohos/releases/download/${VERSION}/${archive}"
echo "347e89b3ace7c18c7707b331ce70801830edeb93f49b33851875c4e3f55fa894  $archive" | sha256sum -c -
mkdir -p "$HOME/.m2/repository"
tar -xzf "$archive" -C "$HOME/.m2/repository"
