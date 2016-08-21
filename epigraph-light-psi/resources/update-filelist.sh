#!/usr/bin/env bash
# this script is supposed to only be run by 'gradle updateFileList'

fileList="$1"

cd ../schema-parser
gradle clean copySources installDist
cd build/install/schema-parser/bin
patch -s -p0 schema-parser ../../../../../epigraph-light-psi/build/schema-parser.patch >/dev/null 2>&1
rm schema-parser.rej 2>/dev/null
JAVA_OPTS="-verbose:class" ./schema-parser test | grep Loaded | grep -i idea | sed -e 's/\./\//g' | awk '{print $2 ".class"}' | sort | uniq > $fileList
