#!/usr/bin/env bash
##
# Deploys artifacts to the repository. The new version is taken from NEW_VERSION variable or inferred from the latest
# version of the main pom in the repository by incrementing its last (e.g. PATCH) part by 1.
# The script is expected to be run from the root of the project.

set -e

##
# Splits the string argument around '.' as delimiter, increments the last part, and echoes incremented and re-assembled
# version.
function bump() {
  # TODO check version argument is well-formed (at theast the last segment being numeric)?
  local PARTS IFS='.'
  read -a PARTS <<< "$1"
  local LAST=$((${#PARTS[*]} - 1))
  PARTS[$LAST]=$((${PARTS[$LAST]} + 1))
  echo "${PARTS[*]}"
}

if [ -z "$NEW_VERSION" ]; then
  # mvn is used here (instead of mvnw) because the latter doesn't respect `--quiet` option and pollutes the output
  RELEASED_VERSION="$(\
    mvn --quiet -pl pom.xml build-helper:released-version \
      exec:exec -Dexec.executable='echo' -Dexec.args='${releasedVersion.version}' \
  )"
  echo "Latest released version: $RELEASED_VERSION"
  NEW_VERSION=$(bump "$RELEASED_VERSION")
fi

echo -------------------------------------------------------------------------------
echo ------ Releasing version $NEW_VERSION
echo -------------------------------------------------------------------------------

set -x

./mvnw --show-version --batch-mode -Dbuildtime.output.log \
  clean deploy -Plight-psi,release -Drevision=$NEW_VERSION -DdeployAtEnd=true

git tag "v$NEW_VERSION" && git push origin "v$NEW_VERSION"
