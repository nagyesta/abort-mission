![Abort-Mission](../../.github/assets/Abort-Mission-logo_export_transparent_640.png)

[![GitHub license](https://img.shields.io/github/license/nagyesta/abort-mission?color=informational)](https://raw.githubusercontent.com/nagyesta/abort-mission/main/LICENSE)
[![Java version](https://img.shields.io/badge/Java%20version-11-yellow?logo=java)](https://img.shields.io/badge/Java%20version-11-yellow?logo=java)
[![latest-release](https://img.shields.io/github/v/tag/nagyesta/abort-mission?color=blue&logo=git&label=releases&sort=semver)](https://github.com/nagyesta/abort-mission/releases)
[![JavaCI](https://img.shields.io/github/actions/workflow/status/nagyesta/abort-mission/gradle.yml?logo=github&branch=main)](https://github.com/nagyesta/abort-mission/actions/workflows/gradle.yml)

# Flight Evaluation Report Generator

[![codecov flighteval](https://img.shields.io/codecov/c/github/nagyesta/abort-mission?label=Coverage:%20FlightEvaluationReport&flag=flighteval&token=I832ZCIONI)](https://img.shields.io/codecov/c/github/nagyesta/abort-mission?label=Coverage:%20FlightEvaluationReport&flag=flighteval&token=I832ZCIONI)

Please find the essentials below or check out [the wiki](https://github.com/nagyesta/abort-mission/wiki) for more
details.

# Purpose

This application is responsible for turning the JSON execution reports generated by Abort-Mission into more
convenient, HTML reports.

Depending on your setup and how you plan to use the information collected, you can either use the Jar alone, or you may
want to integrate it into your build pipeline and automate the report generation as described below.

# Quick-start

## Manually

Abort-Mission can be downloaded from a few Maven repositories. Please head to
[this page](https://github.com/nagyesta/abort-mission/wiki/Configuring-our-repository-for-your-build-system)
to find out more.

**Note:** Please don't forget to set the `abort-mission.report.directory` System property as described
[here](https://github.com/nagyesta/abort-mission/blob/main/mission-control/README.md#system-properties)
in order to have the JSON input generated during your build.

## Gradle

Please find our own Abort-Mission Gradle
plugin [here](https://github.com/nagyesta/abort-mission-gradle-plugin/blob/main/README.md)

## Maven

Please find our own Abort-Mission Maven
plugin [here](https://github.com/nagyesta/abort-mission-maven-plugin/blob/main/README.md)

**Note:** Please don't forget to set the `abort-mission.report.directory` System property as described
[here](https://github.com/nagyesta/abort-mission/blob/main/mission-control/README.md#system-properties)
in order to have the JSON input generated during your build.

# Configuration

| Argument name        | Type    | Description                                                                                                          |
| -------------------- | ------- | -------------------------------------------------------------------------------------------------------------------- |
| --report.input       | File    | The path of the input JSON file                                                                                      |
| --report.output      | File    | The path of the output HTML file                                                                                     |
| --report.relaxed     | boolean | Selects between relaxed and strict schema validation (default: false)                                                |
| --report.failOnError | boolean | Decides whether we need to exit with error in case failures are detected in the test execution data (default: false) |