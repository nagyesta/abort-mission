![Abort-Mission](../../.github/assets/Abort-Mission-logo_export_transparent_640.png)

[![GitHub license](https://img.shields.io/github/license/nagyesta/abort-mission?color=informational)](https://raw.githubusercontent.com/nagyesta/abort-mission/main/LICENSE)
[![Java version](https://img.shields.io/badge/Java%20version-17-yellow?logo=java)](https://img.shields.io/badge/Java%20version-17-yellow?logo=java)
[![latest-release](https://img.shields.io/github/v/tag/nagyesta/abort-mission?color=blue&logo=git&label=releases&sort=semver)](https://github.com/nagyesta/abort-mission/releases)
[![JavaCI](https://img.shields.io/github/actions/workflow/status/nagyesta/abort-mission/gradle.yml?logo=github&branch=main)](https://github.com/nagyesta/abort-mission/actions/workflows/gradle.yml)

[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=nagyesta_abort-mission&metric=coverage)](https://sonarcloud.io/summary/new_code?id=nagyesta_abort-mission)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=nagyesta_abort-mission&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=nagyesta_abort-mission)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=nagyesta_abort-mission&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=nagyesta_abort-mission)
![[Stable](https://img.shields.io/badge/Maturity-stable-green)](https://img.shields.io/badge/Maturity-stable-green)

# Cucumber JVM Booster

Please find the essentials below or check out [the wiki](https://github.com/nagyesta/abort-mission/wiki) for more
details.

# Quick-start

## Dependency

Abort-Mission can be downloaded from a few Maven repositories. Please head to
[this page](https://github.com/nagyesta/abort-mission/wiki/Configuring-our-repository-for-your-build-system)
to find out more.

### Maven

```xml
<dependency>
  <groupId>com.github.nagyesta.abort-mission.boosters</groupId>
  <artifactId>abort.booster-cucumber-jvm</artifactId>
  <version>RELEASE</version>
  <scope>test</scope>
</dependency>
```

### Gradle

```groovy
testImplementation "com.github.nagyesta.abort-mission.boosters:abort.booster-cucumber-jvm:+"
```

## Configuration

The minimal setup consists of only 3 steps in case you are using this booster.

1. Create a Cucumber hook implementing `com.github.nagyesta.abortmission.booster.cucumber.LaunchAbortHook`
   . [See example](src/test/java/com/github/nagyesta/abortmission/booster/cucumber/fueltank/AbortMissionHook.java)
2. Use the plugin named `com.github.nagyesta.abortmission.booster.cucumber.AbortMissionPlugin`
3. Annotate your scenarios with the Abort-Mission tags:
    1. `@AbortMission_Context_ContextName` if you want to use a named context
    2. `@AbortMission_SuppressAbort` if you want to suppress abort decisions
    3. `@AbortMission_SuppressFailure_fully.qualified.name.of.an.Exception` to suppress exception related failure
       reporting

**NOTE:** Please make sure to use `relaxed` schema validation when you are generating the flight evaluation report.

## Examples

- In case you need some basic examples, please feel free to check out [this package](./src/test/java/com/github/nagyesta/abortmission/booster/cucumber)
- Alternatively, you can take a look at the following example project: [Abort-Mission Examples](https://github.com/nagyesta/abort-mission-examples)
