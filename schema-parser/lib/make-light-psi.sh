#!/bin/sh

# THIS FILE IS OBSOLETE, USE GRADLE TASKS IN epigraph-light-psi

# Try to detect IDEA home
[ -z "$IDEA_HOME" ] && IDEA_HOME="/Applications/IntelliJ IDEA CE.app/Contents"
[ ! -d "$IDEA_HOME" ] && IDEA_HOME="/Applications/IntelliJ IDEA.app/Contents"
[ ! -d "$IDEA_HOME" ] && IDEA_HOME="$HOME/idea"
[ ! -d "$IDEA_HOME" ] && IDEA_HOME="$HOME/Applications/IntelliJ IDEA CE.app/Contents"

[ ! -d "$IDEA_HOME" ] && echo "Can't detect IDEA location, please set IDEA_HOME" && exit 1

if [ -z "$GKIT_JAR" ]; then
  # Try to detect plugins location
  [ -z "$GKIT_JAR" ] && GKIT_JAR="$HOME/Library/Application Support/IdeaIC2016.2/GrammarKit/lib/grammar-kit.jar"
  [ ! -f "$GKIT_JAR" ] && GKIT_JAR="$HOME/Library/Application Support/IntelliJIdea2016.2/GrammarKit/lib/grammar-kit.jar"
  [ ! -f "$GKIT_JAR" ] && GKIT_JAR="$HOME/.IdeaIC2016.2/GrammarKit/lib/grammar-kit.jar"
  [ ! -f "$GKIT_JAR" ] && GKIT_JAR="$HOME/.IntelliJIdea2016.2/GrammarKit/lib/grammar-kit.jar"
fi

[ ! -f "$GKIT_JAR" ] && echo "Can't detect grammar-kit.jar location, please install it or set GKIT_JAR" && exit 1

mv light-psi-all.jar light-psi-all_old.jar 2>/dev/null

rm -rf unp 2>/dev/null
mkdir unp
cd unp

cat ../light-psi-filelist.txt | paste -sd \  - | xargs jar xf "$IDEA_HOME/lib/annotations.jar"
cat ../light-psi-filelist.txt | paste -sd \  - | xargs jar xf "$IDEA_HOME/lib/asm.jar"
cat ../light-psi-filelist.txt | paste -sd \  - | xargs jar xf "$IDEA_HOME/lib/asm-all.jar"
cat ../light-psi-filelist.txt | paste -sd \  - | xargs jar xf "$IDEA_HOME/lib/asm-commons.jar"
cat ../light-psi-filelist.txt | paste -sd \  - | xargs jar xf "$IDEA_HOME/lib/automaton.jar"
cat ../light-psi-filelist.txt | paste -sd \  - | xargs jar xf "$IDEA_HOME/lib/extensions.jar"
cat ../light-psi-filelist.txt | paste -sd \  - | xargs jar xf "$IDEA_HOME/lib/guava-17.0.jar"
cat ../light-psi-filelist.txt | paste -sd \  - | xargs jar xf "$IDEA_HOME/lib/idea.jar"
cat ../light-psi-filelist.txt | paste -sd \  - | xargs jar xf "$IDEA_HOME/lib/idea_rt.jar"
cat ../light-psi-filelist.txt | paste -sd \  - | xargs jar xf "$IDEA_HOME/lib/jsr166e.jar"
cat ../light-psi-filelist.txt | paste -sd \  - | xargs jar xf "$IDEA_HOME/lib/openapi.jar"
cat ../light-psi-filelist.txt | paste -sd \  - | xargs jar xf "$IDEA_HOME/lib/picocontainer.jar"
cat ../light-psi-filelist.txt | paste -sd \  - | xargs jar xf "$IDEA_HOME/lib/trove4j.jar"
cat ../light-psi-filelist.txt | paste -sd \  - | xargs jar xf "$IDEA_HOME/lib/util.jar"
cat ../light-psi-filelist.txt | paste -sd \  - | xargs jar xf "$GKIT_JAR"

jar cf ../light-psi-all.jar *

cd ..
rm -rf unp

# vim: set nowrap
