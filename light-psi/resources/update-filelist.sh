#!/usr/bin/env bash
# this script is supposed to only be run by './gradlew updateFileList'

fileList="$1"

cd ../schema-parser # FIXME relative path is probably wrong
../gradlew clean copySources installDist # FIXME relative path is probably wrong
cd build/install/schema-parser/bin
patch -s -p0 schema-parser ../../../../../light-psi/build/schema-parser.patch >/dev/null 2>&1
rm schema-parser.rej 2>/dev/null
JAVA_OPTS="-verbose:class" ./schema-parser test | grep Loaded | grep -i idea | sed -e 's/\./\//g' | awk '{print $2 ".class"}' | sort | uniq > $fileList
