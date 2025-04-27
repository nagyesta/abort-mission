![Abort-Mission](../../.github/assets/Abort-Mission-logo_export_transparent_640.png)

[![GitHub license](https://img.shields.io/github/license/nagyesta/abort-mission?color=informational)](https://raw.githubusercontent.com/nagyesta/abort-mission/main/LICENSE)
[![Java version](https://img.shields.io/badge/Java%20version-17-yellow?logo=java)](https://img.shields.io/badge/Java%20version-17-yellow?logo=java)
[![latest-release](https://img.shields.io/github/v/tag/nagyesta/abort-mission?color=blue&logo=git&label=releases&sort=semver)](https://github.com/nagyesta/abort-mission/releases)
[![JavaCI](https://img.shields.io/github/actions/workflow/status/nagyesta/abort-mission/gradle.yml?logo=github&branch=main)](https://github.com/nagyesta/abort-mission/actions/workflows/gradle.yml)

[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=nagyesta_abort-mission&metric=coverage)](https://sonarcloud.io/summary/new_code?id=nagyesta_abort-mission)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=nagyesta_abort-mission&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=nagyesta_abort-mission)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=nagyesta_abort-mission&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=nagyesta_abort-mission)
![[Stable](https://img.shields.io/badge/Maturity-stable-green)](https://img.shields.io/badge/Maturity-stable-green)

# JUnit Jupiter Booster

Please find the essentials below or check out [the wiki](https://github.com/nagyesta/abort-mission/wiki) for more details.

# Quick-start

## Dependency

Abort-Mission can be downloaded from a few Maven repositories. Please head to
[this page](https://github.com/nagyesta/abort-mission/wiki/Configuring-our-repository-for-your-build-system)
to find out more.

### Maven

```xml
<dependency>
  <groupId>com.github.nagyesta.abort-mission.boosters</groupId>
  <artifactId>abort.booster-junit-jupiter</artifactId>
  <version>RELEASE</version>
  <scope>test</scope>
</dependency>
```


### Gradle

```groovy
testImplementation "com.github.nagyesta.abort-mission.boosters:abort.booster-junit-jupiter:+"
```

## Configuration

The minimal setup consists of only 3 steps in case you are using this booster.

1. Implement [MissionOutline](../../mission-control/src/main/java/com/github/nagyesta/abortmission/core/outline/MissionOutline.java) named as `MissionOutlineDefinition`
   preferably in your root package
2. Annotate your tests with [@LaunchAbortArmed](./src/main/java/com/github/nagyesta/abortmission/booster/jupiter/annotation/LaunchAbortArmed.java)
    1. Make sure the Extension we are adding is the one running first (in case you are using others, like the ones offered by Spring.)
3. Use the additional core annotations or the `@Tag` annotation provided by Jupiter to define your dependencies and group your tests properly

**Note:** Using `PER_CLASS` lifecycle makes countdown abort evaluation less effective as test classes are reused.

## Examples

- In case you need some basic examples, please feel free to check out [this package](./src/test/java/com/github/nagyesta/abortmission/booster/jupiter)
- Alternatively, you can take a look at the following example project: [Abort-Mission Examples](https://github.com/nagyesta/abort-mission-examples)
