const path = require('path');
const fs = require('fs');
const plist = require('plist');
const { ConfigParser } = require('cordova-common');

module.exports = function (context) {
    let projectRoot = context.opts.cordova.project ? context.opts.cordova.project.root : context.opts.projectRoot;
    let configXML = path.join(projectRoot, 'config.xml');
    let configParser = new ConfigParser(configXML);
    
    let appName = configParser.name();
    let infoPlistPath = path.join(projectRoot, 'platforms/ios/' + appName + '/'+ appName +'-info.plist');
    let obj = plist.parse(fs.readFileSync(infoPlistPath, 'utf8'));

    let enableAppTracking = configParser.getPlatformPreference("EnableAppTrackingTransparencyPrompt", "ios");
    if(enableAppTracking == "true" || enableAppTracking == ""){
        let userTrackingDescription = configParser.getPlatformPreference("USER_TRACKING_DESCRIPTION_IOS", "ios");
        if(userTrackingDescription != ""){
            obj['NSUserTrackingUsageDescription'] = userTrackingDescription;
            fs.writeFileSync(infoPlistPath, plist.build(obj));
        }
    }
    else if(enableAppTracking == "false"){
        delete obj['NSUserTrackingUsageDescription'];
        fs.writeFileSync(infoPlistPath, plist.build(obj));
    }

    let collectionEnabled = configParser.getGlobalPreference("ANALYTICS_COLLECTION_ENABLED");
    if (collectionEnabled.toLowerCase() == 'false') {
        obj['FIREBASE_ANALYTICS_COLLECTION_ENABLED'] = false;
    } 

    fs.writeFileSync(infoPlistPath, plist.build(obj));
};