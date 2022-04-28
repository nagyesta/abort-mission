# Security Policy

## Err on the safer side

As Abort-Mission is strictly intended to be a development tool, it is **strongly
recommended to never deploy the Abort-Mission artifacts** together with your 
production service/product. Although this is not ensuring that it won't be ever a 
source you would need to consider as a source of risk, it would certainly make it 
a bit harder to use for malicious actors.

## Supported Versions

The aim is to support the fellow developers who are using this library as much as
possible but at the end of the day, this is a hobby project which is maintained in
my free time. So reality is that the latest version will be supported with security
patches in case vulnerabilities are reported and everything else will be decided
case by case.

[![Supported version](https://img.shields.io/github/v/tag/nagyesta/abort-mission?color=green&logo=git&label=Supported%20version&sort=semver)](https://img.shields.io/github/v/tag/nagyesta/abort-mission?color=green&logo=git&label=Supported%20version&sort=semver)

## Reporting a Vulnerability

In case you have found a vulnerability, please report an [issue here](https://github.com/nagyesta/abort-mission/issues)

Thank you in advance!

## Vulnerability Response

Once a vulnerability is reported, I will try to fix it as soon as I can afford
the time, preferably under less than 60 days from receiving a valid security 
vulnerability report.

In case of vulnerable dependencies, response time depends on the release of the
known safe/fixed dependency version as well. As long as there is no such 
available version, the update activity is considered to be blocked, therefore 
the normal response timeline does not apply.
