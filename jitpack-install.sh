#!/usr/bin/env bash
set -euo pipefail

archive=stately-maven.tar.gz
curl -fL --retry 3 -o "$archive" "https://github.com/gycrosskit/stately-ohos/releases/download/${VERSION}/${archive}"
echo "bd0820305f4fb795127718f902fb4c178f1a49fababd3c955374106587714b9b  $archive" | sha256sum -c -
mkdir -p "$HOME/.m2/repository"
tar -xzf "$archive" -C "$HOME/.m2/repository"
mkdir -p build/release-maven
tar -xzf "$archive" -C build/release-maven

# 在 macOS 归档前正规化 metadata；安装时只解包已校验的相同字节。
