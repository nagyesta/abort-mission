![Abort-Mission](.github/assets/Abort-Mission-logo_export_transparent_640.png)

[![GitHub license](https://img.shields.io/github/license/nagyesta/abort-mission?color=informational)](https://raw.githubusercontent.com/nagyesta/abort-mission/main/LICENSE)
[![Java version](https://img.shields.io/badge/Java%20version-11-yellow?logo=java)](https://img.shields.io/badge/Java%20version-11-yellow?logo=java)
[![latest-release](https://img.shields.io/github/v/tag/nagyesta/abort-mission?color=blue&logo=git&label=releases&sort=semver)](https://github.com/nagyesta/abort-mission/releases)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.nagyesta.abort-mission/abort.mission-control?logo=apache-maven)](https://search.maven.org/search?q=com.github.nagyesta.abort-mission)
[![JavaCI](https://img.shields.io/github/workflow/status/nagyesta/abort-mission/JavaCI?logo=github)](https://img.shields.io/github/workflow/status/nagyesta/abort-mission/JavaCI?logo=github)

[![CII Best Practices](https://bestpractices.coreinfrastructure.org/projects/6017/badge)](https://bestpractices.coreinfrastructure.org/projects/6017)
[![code-climate-maintainability](https://img.shields.io/codeclimate/maintainability/nagyesta/abort-mission?logo=code%20climate)](https://img.shields.io/codeclimate/maintainability/nagyesta/abort-mission?logo=code%20climate)
[![code-climate-tech-debt](https://img.shields.io/codeclimate/tech-debt/nagyesta/abort-mission?logo=code%20climate)](https://img.shields.io/codeclimate/tech-debt/nagyesta/abort-mission?logo=code%20climate)
[![last_commit](https://img.shields.io/github/last-commit/nagyesta/abort-mission?logo=git)](https://img.shields.io/github/last-commit/nagyesta/abort-mission?logo=git)
[![wiki](https://img.shields.io/badge/See-Wiki-informational)](https://github.com/nagyesta/abort-mission/wiki)

Abort-Mission is a lightweight Java library providing flexible test abortion support for test groups to allow fast failures.

## What does this mean?

- Provides a way to group your tests
- Lets you define certain pre-requisites for your tests
- Tracks how each group succeeds or fails
- Uses the collected data to prevent test runs which are predicted to fail already

## Why is this good for you?

If you are using Abort-Mission well, you don't need to wait for long CI runs when you know which tests will fail already
because the DB became inaccessible or someone (not you) messed up...
Saving time on CI and local test runs adds up in small increments not only saving money but reducing the time and resources
used for build. This results in using less cloud runners, reducing electricity needs etc. by the end of the day we can help
save the Planet too! Cool, right?

If you are still not convinced, you can trust the ancient proverb saying:

>"Shoot for the Moon, and even if you miss, you can at least stop running your failing tests early."

## Installation

Abort-Mission can be downloaded from a few Maven repositories. Please head to
[this page](https://github.com/nagyesta/abort-mission/wiki/Configuring-our-repository-for-your-build-system)
to find out more.

To find out which Abort-Mission dependency will you need, it is recommended to start with the booster created for the
test framework you are using. Is your favorite framework missing? Let me know by creating an issue! No promises, but I
will at least take a look.

### Currently supported packages

- [Mission Control](mission-control)
- Boosters
  - [Cucumber JVM](boosters/booster-cucumber-jvm)
  - [JUnit 4](boosters/booster-junit4)
  - [JUnit Jupiter](boosters/booster-junit-jupiter)
  - [TestNG](boosters/booster-testng)
  - [Booster Test Kit](boosters/testkit)
- Mission Report
    - [Flight Evaluation Report](mission-report/flight-evaluation-report)
- Strongback
    - [Strongback Base](strongback/strongback-base)
    - [Strongback H2 Supplier](strongback/strongback-h2-supplier)
    - [Strongback RMI Supplier](strongback/strongback-rmi-supplier)

## Terminology and Lifecycle

Abort-Mission views tests runs similarly to a space launch mission. Due to this, terminology mimics
certain phases of a rocket launch. This section can help you understand what happens when.

### Health metrics

Each test offers a slightly different situation, but the following list will cover all counters we
will rely on when using Abort-Mission.

| Measurement point  | What happens there?                       |
| ------------------ | ----------------------------------------- |
| Countdown failed   | Test instance post processing failed      |
| Countdown abort    | Test instance post processing prevented   |
| Countdown success  | Test instance post processed successfully |
| Mission abort      | Test start prevented / abort after start  |
| Mission failed     | Test failed                               | 
| Mission success    | Test executed successfully                | 

## How it works

The oversimplified model used by this project relies on the following elements:

- A **Mission Outline** defining what Abort-Mission should do in which situation in scope of the **Mission Context**
- A number of **Mission Health Check Matchers** which allow the library to identify which category the test class belongs to
- A similar amount of **Mission Health Check Evaluators** which define thresholds for the decision-making
- A bunch of annotations to help with the configuration such as
  - Defining which of the named Mission Contexts do we want to use
  - Allowing suppression of abort decisions for certain tests
  - Ignoring certain exceptions that are causing failures

From all of these, sooner or later you will need to have a Launch Sequence Template that will act as a controller
which knows how your specific test framework needs to bind with the lifecycle stages we need to maintain. This
is probably the hardest part to pull off, don't worry, if you are using a booster that was created for your
test framework, it should be easy, the booster will do the heavy-lifting (after all that is the job of a booster).

Once you have these, you are probably fine.  
