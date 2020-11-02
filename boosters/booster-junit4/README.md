![Abort-Mission](../../.github/assets/Abort-Mission-logo_export_transparent_640.png)

[![GitHub license](https://img.shields.io/github/license/nagyesta/abort-mission?color=informational)](https://raw.githubusercontent.com/nagyesta/abort-mission/main/LICENSE)
[![Java version](https://img.shields.io/badge/Java%20version-8-yellow?logo=java)](https://img.shields.io/badge/Java%20version-8-yellow?logo=java)
[![latest-release](https://img.shields.io/github/v/tag/nagyesta/abort-mission?color=blue&logo=git&label=releases&sort=semver)](https://github.com/nagyesta/abort-mission/releases)
[![JavaCI](https://img.shields.io/github/workflow/status/nagyesta/abort-mission/JavaCI?logo=github)](https://img.shields.io/github/workflow/status/nagyesta/abort-mission/JavaCI?logo=github)

# JUnit 4 Booster

[![codecov junit4](https://img.shields.io/codecov/c/github/nagyesta/abort-mission?label=Coverage:%20JUnit4&flag=junit4&token=I832ZCIONI)](https://img.shields.io/codecov/c/github/nagyesta/abort-mission?label=Coverage:%20JUnit4&flag=junit4&token=I832ZCIONI)
![[Experimental](https://img.shields.io/badge/Maturity-experimental-red)](https://img.shields.io/badge/Maturity-experimental-red)

Please find the essentials below or check out [the wiki](https://github.com/nagyesta/abort-mission/wiki) for more details. 

# Quick-start

## Dependency

Note: Please don't forget to set up the packages we offer as repository as detailed on
[this page](https://docs.github.com/en/free-pro-team@latest/packages/using-github-packages-with-your-projects-ecosystem).

### Maven

```xml
<dependency>
  <groupId>com.github.nagyesta.abort-mission.boosters</groupId>
  <artifactId>abort.booster-junit4-experimental</artifactId>
  <version>RELEASE</version>
  <scope>test</scope>
</dependency>
```
### Gradle

```groovy
testImplementation "com.github.nagyesta.abort-mission.boosters:abort.booster-junit4-experimental:+"
```

## Configuration

The minimal setup consists of only 4 steps in case you are using this booster.

1. Implement [MissionOutline](../../mission-control/src/main/java/com/github/nagyesta/abortmission/core/outline/MissionOutline.java) named as `MissionOutlineDefinition`
preferably in your root package
2. Annotate your tests with [@LaunchAbortArmed](./src/main/java/com/github/nagyesta/abortmission/booster/junit4/annotation/LaunchAbortArmed.java)
3. Use the [LaunchAbortTestWatcher](./src/main/java/com/github/nagyesta/abortmission/booster/junit4/support/LaunchAbortTestWatcher.java) 
4. Use the additional core annotations or the `@Category` annotation provided by JUnit to define your dependencies and group your tests properly

## Examples

In case you need some basic examples, please feel free to check out [this package](./src/test/java/com/github/nagyesta/abortmission/booster/junit4)
