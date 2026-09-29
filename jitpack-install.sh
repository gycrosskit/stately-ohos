#!/usr/bin/env bash
set -euo pipefail

archive=stately-maven.tar.gz
curl -fL --retry 3 -o "$archive" "https://github.com/gycrosskit/stately-ohos/releases/download/${VERSION}/${archive}?download=1"
echo "166acc28da6d8a5b6d8ba6a238df4decb964bed5199d194671f48da38a8cc196  $archive" | sha256sum -c -
mkdir -p "$HOME/.m2/repository"
tar -xzf "$archive" -C "$HOME/.m2/repository"
