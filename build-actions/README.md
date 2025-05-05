# Build Actions

This folder contains .yaml files for configuring build actions to use in a plugin on ODC with Capacitor. The purpose of these build actions is to provide the same functionality as cordova hooks, but on a Capacitor shell.

## Contents

The file setAnalyticsConfigurations.yaml contains two build actions:

- Android specific. Adds the `<meta-data android:name="firebase_analytics_collection_enabled" android:value="true" />` and `google_analytics_automatic_screen_reporting_enabled` meta-data entries to the app's `AndroidManifest.xml`. This is necessary for the Firebase Analytics plugin to work properly in Android.

- iOS specific. Set `FirebaseAutomaticScreenReportingEnabled` to `true` and `NSUserTrackingUsageDescription` in the app's Info.plist file. This is necessary for the Firebase Analytics plugin to work properly in iOS.


## Outsystems' Usage

1. Copy the build action yaml file (which can contain multiple build actions inside) into the ODC Plugin, placing them in "Data" -> "Resources" and set "Deploy Action" to "Deploy to Target Directory", with target directory empty.
2. Update the Plugin's Extensibility configuration to use the build action.

```json
{
    "buildConfigurations": {
        "buildAction": {
            "config": $resources.buildActionFileName.yaml,
            "parameters": {
                // parameters go here; if there are no parameters then the block can be ommited
            }
        }
    }
}
```