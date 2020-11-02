![Abort-Mission](../.github/assets/Abort-Mission-logo_export_transparent_640.png)

[![GitHub license](https://img.shields.io/github/license/nagyesta/abort-mission?color=informational)](https://raw.githubusercontent.com/nagyesta/abort-mission/main/LICENSE)
[![Java version](https://img.shields.io/badge/Java%20version-8-yellow?logo=java)](https://img.shields.io/badge/Java%20version-8-yellow?logo=java)
[![latest-release](https://img.shields.io/github/v/tag/nagyesta/abort-mission?color=blue&logo=git&label=releases&sort=semver)](https://github.com/nagyesta/abort-mission/releases)
[![JavaCI](https://img.shields.io/github/workflow/status/nagyesta/abort-mission/JavaCI?logo=github)](https://img.shields.io/github/workflow/status/nagyesta/abort-mission/JavaCI?logo=github)


# Mission Control

[![codecov core](https://img.shields.io/codecov/c/github/nagyesta/abort-mission?label=Coverage:%20MissionControl&flag=core&token=I832ZCIONI)](https://img.shields.io/codecov/c/github/nagyesta/abort-mission?label=Coverage:%20MissionControl&flag=core&token=I832ZCIONI)
![[Stable](https://img.shields.io/badge/Maturity-stable-green)](https://img.shields.io/badge/Maturity-stable-green)

This module contains the core functionality offered by Abort-Mission. While the right kind of booster
module is the recommended way to integrate Abort-Mission into your tests, this module can come handy
in case you would like to create your own booster or use the low level API hidden by the boosters
because you need more control. Either way, I hope you will find the one page quick-start leaflet below.

In case you don't find enough information here, consider checking [the wiki](https://github.com/nagyesta/abort-mission/wiki) which will (eventually)
give a full reference guide.

# Quick-start

## Dependency

Note: Please don't forget to set up the packages we offer as repository as detailed on
[this page](https://docs.github.com/en/free-pro-team@latest/packages/using-github-packages-with-your-projects-ecosystem).

### Maven

```xml
<dependency>
  <groupId>com.github.nagyesta.abort-mission</groupId>
  <artifactId>abort.mission-control</artifactId>
  <version>RELEASE</version>
  <scope>test</scope>
</dependency>
```
### Gradle

```groovy
testImplementation "com.github.nagyesta.abort-mission:abort.mission-control:+"
```

## Configuration

In order to configure Abort-Mission, the easiest option would be to follow these steps

1. Implement [MissionOutline](./src/main/java/com/github/nagyesta/abortmission/core/outline/MissionOutline.java) named as `MissionOutlineDefinition`
preferably in your root package
2. Use tha annotations we provide [here](./src/main/java/com/github/nagyesta/abortmission/core/annotation/)
3. Make sure to hook our lifecycle methods by either
   1. using a [LaunchSequenceTemplate](./src/main/java/com/github/nagyesta/abortmission/core/LaunchSequenceTemplate.java) 
   2. or one of the [Callable/runnable implementations here](./src/main/java/com/github/nagyesta/abortmission/core/selfpropelled/) 
4. Figure out how to group your tests and limit the blast radius of each dependency as you go

