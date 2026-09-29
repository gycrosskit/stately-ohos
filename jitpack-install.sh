#!/usr/bin/env bash
set -euo pipefail

archive=stately-maven.tar.gz
curl -fL --retry 3 -o "$archive" "https://github.com/gycrosskit/stately-ohos/releases/download/${VERSION}/${archive}"
echo "9abeeff2117c749fdb566049e5e2c4767634c0a2a38586832e214ab937873f34  $archive" | sha256sum -c -
mkdir -p "$HOME/.m2/repository"
tar -xzf "$archive" -C "$HOME/.m2/repository"
mkdir -p build/release-maven
tar -xzf "$archive" -C build/release-maven
