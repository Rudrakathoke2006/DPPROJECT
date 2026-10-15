#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
rm -rf build
mkdir -p build
find src -name '*.java' -print > build/sources.list
javac -encoding UTF-8 -d build @build/sources.list
java -cp build PresentationTestRunner
