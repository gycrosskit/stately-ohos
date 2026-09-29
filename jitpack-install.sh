#!/usr/bin/env bash
set -euo pipefail

archive=stately-maven.tar.gz
curl -fL --retry 3 -o "$archive" "https://github.com/gycrosskit/stately-ohos/releases/download/${VERSION}/${archive}"
echo "3353ad6b32e45c150fce89b5f9d56dfa900f61a980ba2035170cf1ff53553635  $archive" | sha256sum -c -
mkdir -p "$HOME/.m2/repository"
tar -xzf "$archive" -C "$HOME/.m2/repository"
mkdir -p build/release-maven
tar -xzf "$archive" -C build/release-maven
python3 - <<'PY'
import json
from pathlib import Path
# JitPack rewrites classified source/metadata JAR URLs to missing plain JARs.
changed = 0
for root in (Path.home() / '.m2/repository/com/github/gycrosskit/stately-ohos', Path('build/release-maven')):
    for file in root.rglob('*.module'):
        data = json.loads(file.read_text())
        variants = [v for v in data['variants'] if not v['name'].endswith(('SourcesElements-published', 'MetadataElements-published'))]
        if len(variants) != len(data['variants']):
            data['variants'] = variants
            file.write_text(json.dumps(data, indent=2))
            changed += 1
if not changed:
    raise SystemExit('No JitPack KMP metadata variants were fixed')
PY
