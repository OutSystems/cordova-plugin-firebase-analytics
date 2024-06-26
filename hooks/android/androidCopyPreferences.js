const path = require('path');
const fs = require('fs');
const { ConfigParser } = require('cordova-common');
const { DOMParser, XMLSerializer } = require('@xmldom/xmldom')

module.exports = function (context) {
    var projectRoot = context.opts.cordova.project ? context.opts.cordova.project.root : context.opts.projectRoot;
    var configXML = path.join(projectRoot, 'config.xml');
    var configParser = new ConfigParser(configXML);
    
    var manifestPath = path.join(projectRoot, 'platforms/android/app/src/main/AndroidManifest.xml');

    const doc = new DOMParser().parseFromString(fs.readFileSync(manifestPath, 'utf8'), 'text/xml');

    var collectionEnabled = configParser.getGlobalPreference("ANALYTICS_COLLECTION_ENABLED");    
    if (collectionEnabled.toLowerCase() == 'false') {
        var applicationData = doc.getElementsByTagName('application');
        var newElement = doc.createElement("meta-data android:name=\"firebase_analytics_collection_enabled\" android:value=\""+ collectionEnabled + "\"");        
        applicationData.item(0).appendChild(newElement);
    } 

    const serialized = new XMLSerializer().serializeToString(doc);
    fs.writeFileSync(manifestPath, serialized);
};