![Abort-Mission](../../.github/assets/Abort-Mission-logo_export_transparent_640.png)

[![GitHub license](https://img.shields.io/github/license/nagyesta/abort-mission?color=informational)](https://raw.githubusercontent.com/nagyesta/abort-mission/main/LICENSE)
[![Java version](https://img.shields.io/badge/Java%20version-11-yellow?logo=java)](https://img.shields.io/badge/Java%20version-11-yellow?logo=java)
[![latest-release](https://img.shields.io/github/v/tag/nagyesta/abort-mission?color=blue&logo=git&label=releases&sort=semver)](https://github.com/nagyesta/abort-mission/releases)
[![JavaCI](https://img.shields.io/github/workflow/status/nagyesta/abort-mission/JavaCI?logo=github)](https://img.shields.io/github/workflow/status/nagyesta/abort-mission/JavaCI?logo=github)

# Strongback Base

[![codecov strongback](https://img.shields.io/codecov/c/github/nagyesta/abort-mission?label=Coverage:%20Strongback%20Base&flag=strongback&token=I832ZCIONI)](https://img.shields.io/codecov/c/github/nagyesta/abort-mission?label=Coverage:%20Strongback%20Base&flag=strongback&token=I832ZCIONI)
![[Stable](https://img.shields.io/badge/Maturity-stable-green)](https://img.shields.io/badge/Maturity-stable-green)

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
  <groupId>com.github.nagyesta.abort-mission.strongback</groupId>
  <artifactId>abort.strongback-base</artifactId>
  <version>RELEASE</version>
  <scope>test</scope>
</dependency>
```

### Gradle

```groovy
testImplementation "com.github.nagyesta.abort-mission.strongback:abort.strongback-base:+"
```

## Integration

The base Strongback component is not intended to be integrated on its own. It is more of a foundation piece that can reduce boilerplate in
specific Strongback implementations that offer to augment launch characteristics of the boosters.

With that said, if you have a need to use a currently unsupported Strongback type, please feel free to reuse the code of this base component
by adding it to your classpath. Your own Strongback component can be even compatible with the gradle plugin, or the other Abort-Mission
components as long as you fulfill a few simple rules. Please see these below:

1. Implement the [StrongbackController](./src/main/java/com/github/nagyesta/abortmission/strongback/base/StrongbackController.java)
   interface which should:
    1. Prepare the Strongback (such as starting an external component etc.) when `erect()` is called.
    2. Take care of publishing the launch report from the telemetry collected when `retract()` is called.
    3. Clean-up the Strongback (such as stopping the external service) when `retract()` is called.
2. `strongback-blueprint.xml` must be created as a root Spring XML configuration, and it should define an instance of the controller you
   implemented
3. If your Strongback needs to deploy any parts together with the core Abort-Mission components, for example provide a statistics collector
   or something else, you should make sure you are not introducing unnecessary dependencies on your test classpath.

## Configuration

The base Strongback offers the following configuration properties for the specific implementations to use. These are all relative to
the `abort-mission.telemetry.server.` property prefix.

| Property suffix | Type    | Description                                                                                        |
| --------------- | ------- | -------------------------------------------------------------------------------------------------- |
| `port`          | int     | Specifies the port used by the implementing Strongback.                                            |
| `useExternal`   | boolean | Indicates whether we need to use an externally provided server instead of the embedded. (optional) |
| `password`      | String  | The password needed for server startup and shutdown. (optional)                                    |

If you are using the Gradle plugin, these properties can be supplied by the plugin for the standard configuration.

## Build tool independent steps needed for Strongback lifecycle management

### Erect

You need to put your Strongback implementation, the base Strongback and all of their dependencies on a classpath
(referenced as `strongback_classpath`).

```
java -Dabort-mission.telemetry.server.port=29542 \
     -Dabort-mission.telemetry.server.useExternal=false \
     -Dabort-mission.telemetry.server.password=S3cr3t \
     -cp <strongback_classpath> \
     com.github.nagyesta.abortmission.strongback.StrongbackErectorMain &
```

**Warning:** You must run the above step in the background (keeping it running during the test is executed).

### Retract

You need to put your Strongback implementation, the base Strongback and all of their dependencies on a classpath
(referenced as `strongback_classpath`).

```
java -Dabort-mission.telemetry.server.port=29542 \
     -Dabort-mission.telemetry.server.useExternal=false \
     -Dabort-mission.telemetry.server.password=S3cr3t \
     -Dabort-mission.report.directory=build/reports/abort-mission/ \
     -cp <strongback_classpath> \
     com.github.nagyesta.abortmission.strongback.StrongbackRetractorMain
```
