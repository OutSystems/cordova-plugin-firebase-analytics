var exec = require("cordova/exec");
var PLUGIN_NAME = "FirebaseAnalytics";

module.exports = {
    logEvent: function(name, params) {
        return new Promise(function(resolve, reject) {
            exec(resolve, reject, PLUGIN_NAME, "logEvent", [name, params || {}]);
        });
    },
    setUserId: function(userId) {
        return new Promise(function(resolve, reject) {
            exec(resolve, reject, PLUGIN_NAME, "setUserId", [userId]);
        });
    },
    setUserProperty: function(name, value) {
        return new Promise(function(resolve, reject) {
            if (typeof name !== "string") {
                return reject(new TypeError("User property name must be a string"));
            }

            if (typeof value !== "string") {
                return reject(new TypeError("User property value must be a string"));
            }

            exec(resolve, reject, PLUGIN_NAME, "setUserProperty", [name, value]);
        });
    },
    resetAnalyticsData: function() {
        return new Promise(function(resolve, reject) {
            exec(resolve, reject, PLUGIN_NAME, "resetAnalyticsData", []);
        });
    },
    setEnabled: function(enabled) {
        return new Promise(function(resolve, reject) {
            exec(resolve, reject, PLUGIN_NAME, "setEnabled", [enabled]);
        });
    },
    setCurrentScreen: function(name) {
        return new Promise(function(resolve, reject) {
            exec(resolve, reject, PLUGIN_NAME, "setCurrentScreen", [name]);
        });
    },
    setDefaultEventParameters: function(defaults) {
        return new Promise(function(resolve, reject) {
            if (typeof defaults !== "object") {
                return reject(new TypeError("Defaults must be an object"));
            }

            exec(resolve, reject, PLUGIN_NAME, "setDefaultEventParameters", [defaults || {}]);
        });
    },
    requestTrackingAuthorization: function(showInformation, title, message, buttonTitle, success, error) {
        exec(success, error, PLUGIN_NAME, 'requestTrackingAuthorization', [showInformation, title, message, buttonTitle]);
    },
    logECommerceEvent: function(event, eventParameters, items, success, error) {
        let args = [{event, eventParameters, items}];
        exec(success, error, PLUGIN_NAME, 'logECommerceEvent', args);
    },
    /**
     * setConsent
     *
     * @param {string} consentSettings - A JSON string of an object containing consent settings.
     * @param {function} [success] - Success callback function.
     * @param {function} [error] - Error callback function.
     *
     * @example
     * setConsent({
     *   AD_STORAGE: 'GRANTED',
     *   ANALYTICS_STORAGE: 'DENIED'
     * }, 
     * function() {
     *   console.log('Consent settings updated successfully');
     * }, 
     * function(error) {
     *   console.error('Error updating consent settings:', error);
     * });
     */
    setConsent: function (consentSettings, success, error) {
        exec(success, error, PLUGIN_NAME, 'setConsent', [consentSettings]);
    }
};
