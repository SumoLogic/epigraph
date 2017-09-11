#!/usr/bin/env bash

set -e

cd $WORKSPACE

# ./mvnw clean install -Plight-psi
./mvnw -Plight-psi,main -DdeployAtEnd=true clean deploy -fae
