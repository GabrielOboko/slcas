#!/usr/bin/env bash
# Gabriel's SLCAS - build & run without Maven (plain javac/java).
set -e
cd "$(dirname "$0")"
rm -rf out
mkdir -p out
echo "Compiling..."
find src -name "*.java" > sources.txt
javac -d out @sources.txt
echo "Running..."
java -cp out com.gabriel.slcas.App
