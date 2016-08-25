#!/usr/bin/env bash
# this script is supposed to only be run by 'gradle updateFileList'

fileList="$1"

cd ../lang-parser
gradle clean copySources installDist
cd build/install/lang-parser/bin
patch -s -p0 lang-parser ../../../../../light-psi/build/lang-parser.patch >/dev/null 2>&1
rm lang-parser.rej 2>/dev/null
JAVA_OPTS="-verbose:class" ./lang-parser test | grep Loaded | grep -i idea | sed -e 's/\./\//g' | awk '{print $2 ".class"}' | sort | uniq > $fileList
