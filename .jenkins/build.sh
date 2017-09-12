#!/usr/bin/env bash

set -ex

./mvnw --show-version --batch-mode clean test -Plight-psi --fail-at-end
