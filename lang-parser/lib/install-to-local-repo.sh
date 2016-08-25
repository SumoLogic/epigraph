#!/usr/bin/env bash
BASEDIR=$(dirname "$BASH_SOURCE")

mvn install:install-file \
 -Dfile=$BASEDIR/light-psi-all.jar \
 -DgroupId=com.sumologic.epigraph.3rd-party \
 -DartifactId=light-psi-all \
 -Dversion=0.0-SNAPSHOT \
 -Dpackaging=jar

#mvn deploy:deploy-file \
# -Dfile=lang-parser/lib/light-psi-all.jar \
# -Durl=file:./lang-parser/local-maven-repo/ \
# -DgroupId=com.sumologic.epigraph.3rd-party \
# -DartifactId=light-psi-all \
# -Dversion=0.0-SNAPSHOT \
# -Dpackaging=jar \
# -DupdateReleaseInfo=true
