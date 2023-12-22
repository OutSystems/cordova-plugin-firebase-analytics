# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

The changes documented here do not include those from the original repository.

## [Unreleased]

### 2023-12-22
- Feat: add new e-commerce logging method on Android (https://outsystemsrd.atlassian.net/browse/RMET-2730).

### 2023-12-13
- Feat: add new e-commerce logging method (https://outsystemsrd.atlassian.net/browse/RMET-2729).

## 5.0.0-OS10

### 2023-08-11
- Feat: update firebase core version (https://outsystemsrd.atlassian.net/browse/RMET-2451).

## 5.0.0-OS9

### 2023-01-12
- Feat: Return user's response to prompt [RMET-2112](https://outsystemsrd.atlassian.net/browse/RMET-2112)

## 5.0.0-OS8

### 2022-10-31
- Replaced jcenter with more up to date mavenCentral [RMET-2036](https://outsystemsrd.atlassian.net/browse/RMET-2036)

### 2022-11-10
- Use fixed versions (https://outsystemsrd.atlassian.net/browse/RMET-2045).

## 5.0.0-OS7
### 2022-08-17
- Update Firebase Analytics version to use `21.1.+` (https://outsystemsrd.atlassian.net/browse/RMET-1766).

## 5.0.0-OS6
### 2021-07-12
- Removed unused parameters from RequestTrackingAuthorization (https://outsystemsrd.atlassian.net/browse/RMET-1715)

## 5.0.0-OS5
### 2021-05-16
- Update dependency to firebase-core to have error message improved (https://outsystemsrd.atlassian.net/browse/RMET-1538)

## 5.0.0-OS4
### 2021-05-09
- Made NSUserTrackingUsageDescription optional in .plist file (https://outsystemsrd.atlassian.net/browse/RMET-1547)

### 2021-05-06
- Fixed default value for NSUserTrackingUsageDescription in plugin.xml (https://outsystemsrd.atlassian.net/browse/RMET-1547)

## 5.0.0-OS3
## 2022-04-19
- Hook to add google services dependency to build.gradle. [RMET-1497](https://outsystemsrd.atlassian.net/browse/RMET-1497)

### 2021-04-18
- Added hook to enable the usage of a dynamic message for the NSUserTrackingUsageDescription field for iOS (https://outsystemsrd.atlassian.net/browse/RMET-1496)

## 5.0.0-OS2
### 2021-11-05
- New plugin release to include metadata tag in Extensibility Configurations in the OS wrapper

### 2021-08-24
- Updated Firebase plugin versions to 8.6.0 on iOS and 19.0.+ on Android [RMET-732](https://outsystemsrd.atlassian.net/browse/RMET-732)

### 2021-08-16
- Added App Tracking Transparency popups [RMET-732](https://outsystemsrd.atlassian.net/browse/RMET-732)

### 2021-07-22
- Added Firebase Core Dependency to install config files [RMET-695](https://outsystemsrd.atlassian.net/browse/RMET-695)

## 5.0.0-OS1
### 2021-05-05
- Fix: Fixed plugin pipeline information in configurations.json [RMET-720](https://outsystemsrd.atlassian.net/browse/RMET-720)

## 5.0.0-OS
### 2021-04-08
- Fix: Fixed Android MABS builds for Firebase Analytics being used with OneSignal [RMET-580](https://outsystemsrd.atlassian.net/browse/RMET-580)

### 2021-03-29
- Fix: Fixed hook unzipAndCopyConfigurations

### 2021-03-18
- Feature: added pipelines configuration [RMET-437](https://outsystemsrd.atlassian.net/browse/RMET-437

### 2021-03-11
- Fix: Fixed the configurations hook (unzipAndCopyConfigurations). [RMET-547](https://outsystemsrd.atlassian.net/browse/RMET-547)

### 2021-03-04
- Fix: Changed the way we include Google Services for Android. [RMET-539](https://outsystemsrd.atlassian.net/browse/RMET-539)

### 2021-02-26
- Fix: Fixed MABS builds for both versions of MABS and Sample App connecting to Firebase Project. [RMET-539](https://outsystemsrd.atlassian.net/browse/RMET-539)

### 2021-02-25
- Fix: Removed unnecessary dependency and add gradle configuration file. [RMET-539](https://outsystemsrd.atlassian.net/browse/RMET-539)