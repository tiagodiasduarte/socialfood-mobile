#!/usr/bin/env bash
# Prints the Gradle iosSimulatorArm64Test tasks for one CI shard, so the iOS unit tests can run
# across parallel jobs. Tasks are discovered from a dry run, so new modules are picked up
# automatically. Every task belongs to exactly one shard.
#
# Usage: scripts/ios-test-shard.sh <core|features-1|features-2|app>
set -euo pipefail

shard="${1:?usage: $0 <core|features-1|features-2|app>}"

all=$(./gradlew iosSimulatorArm64Test --dry-run -q | awk '/:iosSimulatorArm64Test SKIPPED/ { print $1 }')

# Feature modules with tests are split alternately between the two feature shards. The api
# modules have no tests (their test tasks are no-ops) and go to the first shard.
feature_tests=$(echo "$all" | grep '^:feature:' | grep -v ':api:' || true)
feature_apis=$(echo "$all" | grep '^:feature:.*:api:' || true)

case "$shard" in
  core) echo "$all" | grep '^:core:' || true ;;
  features-1) { echo "$feature_apis"; echo "$feature_tests" | awk 'NR % 2 == 1'; } | grep . || true ;;
  features-2) echo "$feature_tests" | awk 'NR % 2 == 0' ;;
  app) echo "$all" | grep -vE '^:(core|feature):' || true ;;
  *) echo "unknown shard: $shard" >&2; exit 1 ;;
esac
