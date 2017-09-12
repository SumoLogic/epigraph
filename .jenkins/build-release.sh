#!/usr/bin/env bash
set -e

[ -z "$WORKSPACE" ] || cd "$WORKSPACE" # TODO not sure this is at all needed

##
# Splits the argument around '.' as delimiter, increments the last part, and echoes incremented and re-assembled version
function bump() {
  local PARTS IFS='.'
  read -a PARTS <<< "$1"
  local LAST=$((${#PARTS[*]} - 1))
  PARTS[$LAST]=$((${PARTS[$LAST]} + 1))
  echo "${PARTS[*]}"
}

if [ -z "$NEW_VERSION" ]; then
  RELEASED_VERSION="$(\
    mvn --quiet -pl pom.xml build-helper:released-version \
      exec:exec -Dexec.executable='echo' -Dexec.args='${releasedVersion.version}' \
  )"
  # TODO check version is well-formed?
  echo "Latest released version: $RELEASED_VERSION"
  NEW_VERSION=$(bump "$RELEASED_VERSION")
fi

echo -------------------------------------------------------------------------------
echo ------ Releasing version $NEW_VERSION
echo -------------------------------------------------------------------------------

set -x

./mvnw --show-version -Dbuildtime.output.log \
  clean deploy -Plight-psi,release -Drevision=$NEW_VERSION -DdeployAtEnd=true

git tag "release_$NEW_VERSION" && git push origin "release_$NEW_VERSION" # TODO use "vX.Y.Z" tags
