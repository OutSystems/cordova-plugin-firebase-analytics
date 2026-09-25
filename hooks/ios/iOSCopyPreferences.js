const path = require('path');
const fs = require('fs');
const plist = require('plist');
const { ConfigParser } = require('cordova-common');

module.exports = function (context) {
    let projectRoot = context.opts.cordova.project ? context.opts.cordova.project.root : context.opts.projectRoot;
    let configXML = path.join(projectRoot, 'config.xml');
    let configParser = new ConfigParser(configXML);
    
    // cordova-ios 8+ uses 'App' as the fixed project folder name and 'App-Info.plist';
    // cordova-ios <8 uses the app name as the project folder name.
    let infoPlistPath = path.join(projectRoot, 'platforms/ios/App/App-Info.plist');
    if (!fs.existsSync(infoPlistPath)) {
        let appName = configParser.name();
        infoPlistPath = path.join(projectRoot, 'platforms/ios/' + appName + '/' + appName + '-Info.plist');
    }
    let obj = plist.parse(fs.readFileSync(infoPlistPath, 'utf8'));


    // set NSUserTrackingUsageDescription if EnableAppTrackingTransparencyPrompt is true
    let enableAppTracking = configParser.getPlatformPreference("EnableAppTrackingTransparencyPrompt", "ios");
    if(enableAppTracking == "true" || enableAppTracking == ""){
        let userTrackingDescription = configParser.getPlatformPreference("USER_TRACKING_DESCRIPTION_IOS", "ios");
        if(userTrackingDescription != ""){
            obj['NSUserTrackingUsageDescription'] = userTrackingDescription;
        }
    }
    else if(enableAppTracking == "false"){
        delete obj['NSUserTrackingUsageDescription'];
    }

    let collectionEnabled = configParser.getGlobalPreference("ANALYTICS_COLLECTION_ENABLED");
    if (collectionEnabled.toLowerCase() == 'false') {
        obj['FIREBASE_ANALYTICS_COLLECTION_ENABLED'] = false;
    }

    fs.writeFileSync(infoPlistPath, plist.build(obj));
};