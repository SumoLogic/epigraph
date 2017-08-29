#!/usr/bin/env sh

cd $WORKSPACE
./mvnw -DdeployAtEnd=true clean deploy -fae
