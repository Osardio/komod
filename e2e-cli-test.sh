#!/usr/bin/env bash
set -euo pipefail

# E2E smoke test for Komod CLI.
#
# Usage: ./e2e-cli-test.sh
#
# Assumes `java` on PATH is JDK 25 (or JAVA_HOME points to it).

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"

E2E_DIR="$ROOT_DIR/e2e"
TARGET_SRC="$ROOT_DIR/komod-sample/target"
TARGET_DST="$E2E_DIR/target"

echo "==> Publishing komod-core, komod-java and komod-sample to ~/.m2"
"$ROOT_DIR/gradlew" :komod-core:publishToMavenLocal :komod-java:publishToMavenLocal :komod-sample:publishToMavenLocal --console=plain

echo "==> Building CLI distribution (installDist)"
"$ROOT_DIR/gradlew" :komod-cli:installDist --console=plain

CLI_BIN="$ROOT_DIR/komod-cli/build/install/komod/bin/komod"
if [[ ! -x "$CLI_BIN" ]]; then
    echo "ERROR: komod launcher not found at $CLI_BIN" >&2
    exit 1
fi

VERSION="$(grep 'version = "' "$ROOT_DIR/komod-sample/build.gradle.kts" | head -1 | sed 's/.*version = "\(.*\)".*/\1/')"
if [[ -z "$VERSION" ]]; then
    echo "ERROR: could not read version from komod-sample/build.gradle.kts" >&2
    exit 1
fi

# Prepare a clean e2e working copy (ignored by git)
rm -rf "$E2E_DIR"
mkdir -p "$E2E_DIR"
cp -r "$TARGET_SRC" "$TARGET_DST"

echo "==> Running CLI"
"$CLI_BIN" \
    -a com.osardio.komod:komod-sample:"$VERSION" \
    -m com.osardio.komod.sample.SampleModification \
    -t "$TARGET_DST" \
    -v

echo "==> Resulting FooService.java:"
cat "$TARGET_DST/com/example/FooService.java"
