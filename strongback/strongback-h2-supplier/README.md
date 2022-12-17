![Abort-Mission](../../.github/assets/Abort-Mission-logo_export_transparent_640.png)

[![GitHub license](https://img.shields.io/github/license/nagyesta/abort-mission?color=informational)](https://raw.githubusercontent.com/nagyesta/abort-mission/main/LICENSE)
[![Java version](https://img.shields.io/badge/Java%20version-11-yellow?logo=java)](https://img.shields.io/badge/Java%20version-11-yellow?logo=java)
[![latest-release](https://img.shields.io/github/v/tag/nagyesta/abort-mission?color=blue&logo=git&label=releases&sort=semver)](https://github.com/nagyesta/abort-mission/releases)
[![JavaCI](https://img.shields.io/github/actions/workflow/status/nagyesta/abort-mission/gradle.yml?logo=github&branch=main)](https://img.shields.io/github/actions/workflow/status/nagyesta/abort-mission/gradle.yml?logo=github&branch=main)

# Strongback H2 Supplier

[![codecov strongback](https://img.shields.io/codecov/c/github/nagyesta/abort-mission?label=Coverage:%20Strongback%20H2&flag=h2&token=I832ZCIONI)](https://img.shields.io/codecov/c/github/nagyesta/abort-mission?label=Coverage:%20Strongback%20H2&flag=h2&token=I832ZCIONI)
![[Stable](https://img.shields.io/badge/Maturity-stable-green)](https://img.shields.io/badge/Maturity-stable-green)

Please find the essentials below or check out [the wiki](https://github.com/nagyesta/abort-mission/wiki) for more details.

This component aims to allow Abort-Mission use even in case you are using JVM forks. Please note, that it might be suboptimal to rely on
this Strongback in case you are not using JVM forks. This is due to the performance hit taken
(~4-10 ms per DB call on a laptop).

# Quick-start

## Dependency

Abort-Mission can be downloaded from a few Maven repositories. Please head to
[this page](https://github.com/nagyesta/abort-mission/wiki/Configuring-our-repository-for-your-build-system)
to find out more.

### Maven

```xml
<dependency>
  <groupId>com.github.nagyesta.abort-mission.strongback</groupId>
  <artifactId>abort.strongback-h2-supplier</artifactId>
  <version>RELEASE</version>
  <scope>test</scope>
</dependency>
```

### Gradle

```groovy
testImplementation "com.github.nagyesta.abort-mission.strongback:abort.strongback-h2-supplier:+"
```

## Integration

Integrating the H2 supplier Strongback is a simple addition once Abort-Mission integration is already done.

### Strongback lifecycle

#### Gradle

The Abort-Mission Gradle plugin is capable of providing the standard configuration for the lifecycle of the Strongback before and after the
tests. In order to start actually utilizing it, please complete the **Test integration** configuration steps below.

#### Maven

There is no Strongback support in case of the Maven plugin at the moment. The recommended option is to use one of the plugins available for
Java execution and take care of erecting and retracting the Strongback as defined [here](../strongback-base/README.md).

## Test integration

You need to use an instance of the H2 specific statistics collector,
[H2BackedStageStatisticsCollectorFactory](./src/main/java/com/github/nagyesta/abortmission/strongback/h2/stats/H2BackedStageStatisticsCollectorFactory.java)
, in order to send over and store the measurement data in the H2 database started by the H2 Strongback. This will require a Db connection as
well. You can
use [H2DataSourceProvider#createDefaultDataSource()](./src/main/java/com/github/nagyesta/abortmission/strongback/h2/server/H2DataSourceProvider.java)
to obtain one.

```java
final StageStatisticsCollectorFactory factory = 
        new H2BackedStageStatisticsCollectorFactory(
                "contextName", H2DataSourceProvider.createDefaultDataSource());

PercentageBasedMissionHealthCheckEvaluator evaluator = percentageBasedEvaluator(matcher, factory)
        .abortThreshold(ABORT_THRESHOLD).build();
ReportOnlyMissionHealthCheckEvaluator noop = reportOnlyEvaluator(matcher, factory)
        .build();
```

Provided that the Strongback lifecycle (and configuration) is managed properly, and there is no network issue preventing you from
connecting, this should be enough for you to store your measurements externally.
