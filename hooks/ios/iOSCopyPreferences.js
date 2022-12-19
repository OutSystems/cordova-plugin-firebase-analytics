const path = require('path');
const fs = require('fs');
const plist = require('plist');
const { ConfigParser } = require('cordova-common');

module.exports = function (context) {
    var projectRoot = context.opts.cordova.project ? context.opts.cordova.project.root : context.opts.projectRoot;
    var configXML = path.join(projectRoot, 'config.xml');
    var configParser = new ConfigParser(configXML);
    var enableAppTracking = configParser.getPlatformPreference("EnableAppTrackingTransparencyPrompt", "ios");

    var appNamePath = path.join(projectRoot, 'config.xml');
    var appNameParser = new ConfigParser(appNamePath);
    var appName = appNameParser.name();
    var infoPlistPath = path.join(projectRoot, 'platforms/ios/' + appName + '/'+ appName +'-info.plist');
    var obj = plist.parse(fs.readFileSync(infoPlistPath, 'utf8'));

    if(enableAppTracking == "true" || enableAppTracking == ""){
        var userTrackingDescription = configParser.getPlatformPreference("USER_TRACKING_DESCRIPTION_IOS", "ios");
        if(userTrackingDescription != ""){
            obj['NSUserTrackingUsageDescription'] = userTrackingDescription;
            fs.writeFileSync(infoPlistPath, plist.build(obj));
        }
    }
    else if(enableAppTracking == "false"){
        delete obj['NSUserTrackingUsageDescription'];
        fs.writeFileSync(infoPlistPath, plist.build(obj));
    }
};
