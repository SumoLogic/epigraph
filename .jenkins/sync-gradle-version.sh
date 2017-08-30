#!/usr/bin/env sh

# synchronizes gradle version with maven version, although removing -SNAPSHOT
# since gradle plugins can't be snapshotted
# see https://github.com/gradle/gradle/blob/v4.0.0-milestone-1/subprojects/plugin-use/src/main/java/org/gradle/plugin/use/resolve/internal/ArtifactRepositoryPluginResolver.java

version=$(grep -m 1 version pom.xml | sed -e 's/.*>\(.*\)-SNAPSHOT.*/\1/')
echo "changing gradle version to $version"

perl -pi -e "s/epigraphVersion = .*/epigraphVersion = $version/" gradle.properties

find . -name "build.gradle" | xargs perl -pi -e "s/id 'ws.epigraph' version .*/id 'ws.epigraph' version '$version'/"
find . -name "build.gradle" | xargs perl -pi -e "s/id 'ws.epigraph.java' version .*/id 'ws.epigraph.java' version '$version'/"
