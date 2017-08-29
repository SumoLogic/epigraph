# Epigraph
[![Build Status](https://travis-ci.org/SumoLogic/epigraph.svg)](https://travis-ci.org/SumoLogic/epigraph)
[![codecov.io](https://codecov.io/github/SumoLogic/epigraph/coverage.svg)](https://codecov.io/github/SumoLogic/epigraph)

Under construction

- [Roadmap](roadmap.md)
- [Todo](todo.md)
- [IDEA plugin snapshot](https://github.com/SumoLogic/epigraph/files/1181584/epigraph-idea-plugin-0.0.5.zip)


### First-time Maven build bootstrap:
- In project root folder run: `./mvnw clean install -Plight-psi`
- Build the rest normally, e.g.: `./mvnw clean test`

### First-time Gradle build bootstrap:
- In project root folder run: `./gradlew -c settings-bootstrap.gradle publishGradlePlugins`
- Build the rest normally, e.g.: `./gradlew clean build`

### IntelliJ IDEA project set-up (maven):
- In project root folder run: `./mvnw clean install -Plight-psi,main`
  (installs current development version of Epigraph Maven plugin(s) to make these available to IDEA builds)
- In IDEA menu: `File` > `Open...` > Select project root folder
