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

            console.log("ANALYTICS: inside parseStringPromise");

            const appNode = result.manifest.application[0];
            appNode['meta-data'] = appNode['meta-data'] || [];
            const metadata = appNode['meta-data'];

            let updated = false;

            // find existing entry index
            const index = metadata.findIndex(item => 
                item['$']?.['android:name'] === 'firebase_analytics_collection_enabled'
            );

            console.log("ANALYTICS: after findIndex");

            if (index !== -1) {
                console.log("ANALYTICS: entry exists, will check if it's true or false");
                // entry exists, check if we should update it
                if (metadata[index]['$']['android:value'] === 'true') {
                    metadata[index]['$']['android:value'] = 'false';
                    updated = true;
                    console.log("ANALYTICS: entry existed as true, changed to false");
                }
            } else {
                console.log("ANALYTICS: entry didn't exist, will push new one")
                // entry doesn't exist, add it
                metadata.push({
                    '$': {
                        'android:name': 'firebase_analytics_collection_enabled',
                        'android:value': 'false'
                     }
                });
                updated = true;
            }

            if (updated) {
                console.log("ANALYTICS: updated is true so we'll write");
                const builder = new xml2js.Builder();
                const xml = builder.buildObject(result);
                fs.writeFileSync(manifestPath, xml);
            }
            console.log("ANALYTICS: end of parseStringPromise");
        })
        .catch((err) => {
            console.log("ANALYTICS: entered catch block");
            throw new Error (`OUTSYSTEMS_PLUGIN_ERROR: Something went wrong while parsing the AndroidManifest.xml file. Please check the logs for more information.`);
        });
    } else {
        console.log("ANALYTICS: entered else so will do nothing");
        defer.resolve();
    }

    return defer.promise;
};