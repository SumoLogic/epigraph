#!/usr/bin/env bash

set -e

cd $WORKSPACE

./mvnw clean install -Plight-psi
./mvnw -DdeployAtEnd=true clean deploy -fae
