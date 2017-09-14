# Epigraph
[![Build Status](https://travis-ci.org/SumoLogic/epigraph.svg?branch=master)](https://travis-ci.org/SumoLogic/epigraph/branches)
[![codecov.io](https://codecov.io/gh/SumoLogic/epigraph/branch/master/graphs/badge.svg)](https://codecov.io/gh/SumoLogic/epigraph/branch/master)
[![License](https://img.shields.io/github/license/SumoLogic/epigraph.svg)](LICENSE.md)

Under construction

- [Roadmap](roadmap.md)
- [Todo](todo.md)
- [IDEA plugin snapshot](https://github.com/SumoLogic/epigraph/files/1181584/epigraph-idea-plugin-0.0.5.zip)


### First-time Maven build bootstrap:

- In the project root folder run: `./mvnw clean install -Plight-psi,-main`
- Build the rest normally, e.g.: `./mvnw clean test`


### First-time Gradle build bootstrap:

- In project root folder run: `./gradlew -c settings-bootstrap.gradle publishGradlePlugins`
- Build the rest normally, e.g.: `./gradlew clean build`


### IntelliJ IDEA project set-up (Maven):

- In the project root folder run:

  `./mvnw clean install -Plight-psi dependency:sources`

  Faster way:

  `./mvnw clean install -am -pl maven-plugin,java/maven-plugin -Plight-psi -Dmaven.test.skip`

  (installs current development version of Epigraph Maven plugin(s) - to make these available to IDEA builds)

- In IDEA menu: `File` > `Open...` > Select project root folder
