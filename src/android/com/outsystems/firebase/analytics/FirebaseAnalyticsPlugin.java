package com.outsystems.plugins.firebase.analytics;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import by.chemerisuk.cordova.support.CordovaMethod;
import by.chemerisuk.cordova.support.ReflectiveCordovaPlugin;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.outsystems.firebase.analytics.OSFANLManager;
import com.outsystems.firebase.analytics.model.ConsentType;
import com.outsystems.firebase.analytics.model.ConsentStatus;
import com.outsystems.firebase.analytics.model.OSFANLError;
import com.outsystems.firebase.analytics.model.OSFANLEventOutputModel;

import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;


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
    private void setConsent(String consentSetting, CallbackContext callbackContext) throws JSONException {
        
        try {
            JSONArray consentSettings = new JSONArray(consentSetting);

            Map<FirebaseAnalytics.ConsentType, FirebaseAnalytics.ConsentStatus> consentMap = new HashMap<>();

            for (int i = 0; i < consentSettings.length(); i++) {
                JSONObject consentItem = consentSettings.getJSONObject(i);
                int typeValue = consentItem.getInt("Type");
                int statusValue = consentItem.getInt("Status");

                FirebaseAnalytics.ConsentType consentType = ConsentType.fromInt(typeValue);
                FirebaseAnalytics.ConsentStatus consentStatus = ConsentStatus.fromInt(statusValue);

                if (consentType != null) {
                    if (consentStatus != null) {
                        if (consentMap.containsKey(consentType)) {
                            throw OSFANLError.Companion.duplicateItemsIn("ConsentSettings");
                        }
                        consentMap.put(consentType, consentStatus);
                    } else {
                        throw OSFANLError.Companion.invalidType("Consent Status of " + consentType, "GRANTED, or DENIED");
                    }
                } else {
                    throw OSFANLError.Companion.invalidType("Consent Type", "AD_PERSONALIZATION, AD_STORAGE, AD_USER_DATA, or ANALYTICS_STORAGE");
                }
            }

            if (!consentMap.isEmpty()) {
                this.firebaseAnalytics.setConsent(consentMap);
                callbackContext.success();
            } else {
                throw OSFANLError.Companion.missing("ConsentSettings");
            }
        } catch (OSFANLError e) {
            JSONObject result = new JSONObject();
            result.put("code", e.getCode());
            result.put("message", e.getMessage());
            callbackContext.error(result);
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