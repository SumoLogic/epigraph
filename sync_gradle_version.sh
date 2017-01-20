#!/usr/bin/env sh

# synchronizes gradle version with maven version, although removing -SNAPSHOT
# since gradle plugins can't be snapshotted

version=$(grep -m 1 version pom.xml | sed -e 's/.*>\(.*\)-SNAPSHOT.*/\1/')
echo "changing version to $version"

perl -pi -e "s/epigraphVersion = .*/epigraphVersion = $version/" gradle.properties

find . -name "build.gradle" | xargs perl -pi -e "s/id 'ws.epigraph' version .*/id 'ws.epigraph' version '$version'/"
find . -name "build.gradle" | xargs perl -pi -e "s/id 'ws.epigraph.java' version .*/id 'ws.epigraph.java' version '$version'/"
