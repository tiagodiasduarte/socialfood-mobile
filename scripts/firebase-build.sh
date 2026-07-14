#!/bin/bash
set -e

if ! gh auth status &>/dev/null; then
  echo "Not authenticated. Logging in with token..."
  if [ -z "$GITHUB_SCRIPT_TOKEN" ]; then
    echo "Error: GITHUB_SCRIPT_TOKEN env var is not set."
    exit 1
  fi
  echo "$GITHUB_SCRIPT_TOKEN" | gh auth login --with-token
fi

echo "Triggering Firebase build on develop..."
gh workflow run firebase.yml --ref develop

echo "Waiting for run to start..."
sleep 3

gh run list --workflow=firebase.yml --limit 1

echo ""
echo "Watching run..."
gh run watch