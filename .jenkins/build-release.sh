#!/usr/bin/env bash

set -e

cd $WORKSPACE

NEW_VERSION=$1

if [ -z "$NEW_VERSION" ]; then
  echo "autodetecting next version"
  v=`./mvnw -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive exec:exec | tail -n 1`
  [[ $v =~ ^([0-9]+)\.([0-9]+)\.([0-9]+)-SNAPSHOT ]] || ( echo "error parsing version"; exit -1 )
  major=${BASH_REMATCH[1]}
  minor=${BASH_REMATCH[2]}
  patch=${BASH_REMATCH[3]}
  NEW_VERSION="${major}.${minor}.$((patch+1))"
fi

NEW_VERSION="${NEW_VERSION}-SNAPSHOT"
echo "New version: ${NEW_VERSION}"

echo "removing -SNAPSHOT from current version"
./mvnw build-helper:parse-version versions:set -DgroupId=ws.epigraph -DoldVersion='${project.version}' -DnewVersion='${parsedVersion.majorVersion}.${parsedVersion.minorVersion}.${parsedVersion.incrementalVersion}'

echo "building"
./mvnw clean install -Plight-psi
./mvnw -DdeployAtEnd=true clean deploy -fae

echo "committing version change"
./mvnw scm:checkin -Dmessage='release ${project.version} version change' -DpushChanges=false

echo "tagging"
./mvnw scm:tag -Dtag='release_${project.version}' -DpushChanges=false

echo "setting new version"
./mvnw versions:set -DgroupId=ws.epigraph -DoldVersion='${project.version}' -DnewVersion="${NEW_VERSION}"
./mvnw scm:checkin -Dmessage="${NEW_VERSION} version change" -DpushChanges=false

echo "synchronizing gradle version"
.jenkins/sync-gradle-version.sh
# git add -u
# git commit -m "gradle version sync"
./mvnw scm:checkin -Dmessage='gradle version sync' -DpushChanges=true

echo "pushing changes to git"
git push origin master
git push origin master --tags
