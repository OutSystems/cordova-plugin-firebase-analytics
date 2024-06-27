const path = require('path');
const fs = require('fs');
const { ConfigParser } = require('cordova-common');
const xml2js = require('xml2js');
const q = require('q');

module.exports = function (context) {
    let projectRoot = context.opts.cordova.project ? context.opts.cordova.project.root : context.opts.projectRoot;
    let configXML = path.join(projectRoot, 'config.xml');
    let configParser = new ConfigParser(configXML);
    
    let manifestPath = path.join(projectRoot, 'platforms/android/app/src/main/AndroidManifest.xml');

    let defer = q.defer();

    let collectionEnabled = configParser.getGlobalPreference("ANALYTICS_COLLECTION_ENABLED");    
    if (collectionEnabled.toLowerCase() == 'false') {
        let parser = new xml2js.Parser();
        parser.parseStringPromise(fs.readFileSync(manifestPath, 'utf8')).then((result) => {
            let metadata = result.manifest.application[0]['meta-data'];
            metadata.push({
                '$': {
                    'android:name': 'firebase_analytics_collection_enabled',
                    'android:value': 'false'
                }
            })

            let builder = new xml2js.Builder();
            let xml = builder.buildObject(result);

            fs.writeFileSync(manifestPath, xml, (err) => {
                throw new Error (`OUTSYSTEMS_PLUGIN_ERROR: Something went wrong while saving the AndroidManifest.xml file. Please check the logs for more information.`);
            });

            defer.resolve();
        })
        .catch((err) => {
            throw new Error (`OUTSYSTEMS_PLUGIN_ERROR: Something went wrong while parsing the AndroidManifest.xml file. Please check the logs for more information.`);
        });
    } else {
        defer.resolve();
    }

    return defer.promise;
};