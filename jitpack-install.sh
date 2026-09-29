#!/usr/bin/env bash
set -euo pipefail

archive=stately-maven.tar.gz
curl -fL --retry 3 -o "$archive" "https://github.com/gycrosskit/stately-ohos/releases/download/${VERSION}/${archive}?download=1"
echo "9ce6916b0d42dbf58389c86bd4d3ef51c4a1e7542c99370cf58676fac6360790  $archive" | sha256sum -c -
mkdir -p "$HOME/.m2/repository"
tar -xzf "$archive" -C "$HOME/.m2/repository"
mkdir -p build/release-maven
tar -xzf "$archive" -C build/release-maven
