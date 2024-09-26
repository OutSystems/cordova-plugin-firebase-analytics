package com.outsystems.plugins.firebase.analytics;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import by.chemerisuk.cordova.support.CordovaMethod;
import by.chemerisuk.cordova.support.ReflectiveCordovaPlugin;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.outsystems.firebase.analytics.OSFANLManager;
import com.outsystems.firebase.analytics.model.OSFANLError;
import com.outsystems.firebase.analytics.model.OSFANLEventOutputModel;

import org.apache.cordova.CallbackContext;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;


public class FirebaseAnalyticsPlugin extends ReflectiveCordovaPlugin {
    private static final String TAG = "FirebaseAnalyticsPlugin";

    private FirebaseAnalytics firebaseAnalytics;

    private OSFANLManager manager = new OSFANLManager();

    @Override
    protected void pluginInitialize() {
        Log.d(TAG, "Starting Firebase Analytics plugin");
        Context context = this.cordova.getActivity().getApplicationContext();
        this.firebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    @CordovaMethod
    private void logEvent(String name, JSONObject params, CallbackContext callbackContext) throws JSONException {
        this.firebaseAnalytics.logEvent(name, parse(params));
        callbackContext.success();
    }

    @CordovaMethod
    private void setUserId(String userId, CallbackContext callbackContext) {
        this.firebaseAnalytics.setUserId(userId);

        callbackContext.success();
    }

    @CordovaMethod
    private void setUserProperty(String name, String value, CallbackContext callbackContext) {
        this.firebaseAnalytics.setUserProperty(name, value);

        callbackContext.success();
    }

    @CordovaMethod
    private void resetAnalyticsData(CallbackContext callbackContext) {
        this.firebaseAnalytics.resetAnalyticsData();

        callbackContext.success();
    }

    @CordovaMethod
    private void setEnabled(boolean enabled, CallbackContext callbackContext) {
        this.firebaseAnalytics.setAnalyticsCollectionEnabled(enabled);

        callbackContext.success();
    }

    @CordovaMethod
    private void setCurrentScreen(String screenName, CallbackContext callbackContext) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);

        callbackContext.success();
    }

    @CordovaMethod
    private void setDefaultEventParameters(JSONObject params, CallbackContext callbackContext) throws JSONException {
        this.firebaseAnalytics.setDefaultEventParameters(parse(params));

        callbackContext.success();
    }

    @CordovaMethod
    private void requestTrackingAuthorization(JSONObject params, CallbackContext callbackContext) throws JSONException {
        //Does nothing. This is an iOS specific method.
        callbackContext.success();
    }

    @CordovaMethod
    private void logECommerceEvent(JSONObject params, CallbackContext callbackContext) throws JSONException {
        try {
            OSFANLEventOutputModel output = manager.buildOutputEventFromInputJSON(params);
            this.firebaseAnalytics.logEvent(output.getName(), output.getParameters());
            callbackContext.success();
        } catch (OSFANLError e) {
            JSONObject result = new JSONObject();
            result.put("code", e.getCode());
            result.put("message", e.getMessage());
            callbackContext.error(result);
        }
    }

    @CordovaMethod
    private void setConsent(JSONObject consentSettings, CallbackContext callbackContext) throws JSONException {
        if (consentSettings == null) {
            callbackContext.error("Invalid input: expected a dictionary");
            return;
        }

        Map<FirebaseAnalytics.ConsentType, FirebaseAnalytics.ConsentStatus> consentMap = new HashMap<>();
        
        Iterator<String> keys = consentSettings.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = consentSettings.getString(key);
            
            FirebaseAnalytics.ConsentType consentType = getConsentType(key);
            FirebaseAnalytics.ConsentStatus consentStatus = getConsentStatus(value);
            
            if (consentType != null && consentStatus != null) {
                consentMap.put(consentType, consentStatus);
            } else {
                Log.w(TAG, "Invalid consent type or status for key: " + key);
            }
        }
        
        if (!consentMap.isEmpty()) {
            this.firebaseAnalytics.setConsent(consentMap);
            callbackContext.success();
        } else {
            callbackContext.error("No valid consent types provided");
        }
    }

    private FirebaseAnalytics.ConsentType getConsentType(String type) {
        switch (type) {
            case "AD_STORAGE":
                return FirebaseAnalytics.ConsentType.AD_STORAGE;
            case "ANALYTICS_STORAGE":
                return FirebaseAnalytics.ConsentType.ANALYTICS_STORAGE;
            case "AD_USER_DATA":
                return FirebaseAnalytics.ConsentType.AD_USER_DATA;
            case "AD_PERSONALIZATION":
                return FirebaseAnalytics.ConsentType.AD_PERSONALIZATION;
            default:
                return null;
        }
    }

    private FirebaseAnalytics.ConsentStatus getConsentStatus(String status) {
        switch (status) {
            case "GRANTED":
                return FirebaseAnalytics.ConsentStatus.GRANTED;
            case "DENIED":
                return FirebaseAnalytics.ConsentStatus.DENIED;
            default:
                return null;
        }
    }

    private static Bundle parse(JSONObject params) throws JSONException {
        Bundle bundle = new Bundle();
        Iterator<String> it = params.keys();

        while (it.hasNext()) {
            String key = it.next();
            Object value = params.get(key);

            if (value instanceof String) {
                bundle.putString(key, (String)value);
            } else if (value instanceof Integer) {
                bundle.putInt(key, (Integer)value);
            } else if (value instanceof Double) {
                bundle.putDouble(key, (Double)value);
            } else if (value instanceof Long) {
                bundle.putLong(key, (Long)value);
            } else {
                Log.w(TAG, "Value for key " + key + " not one of (String, Integer, Double, Long)");
            }
        }

        return bundle;
    }
}
