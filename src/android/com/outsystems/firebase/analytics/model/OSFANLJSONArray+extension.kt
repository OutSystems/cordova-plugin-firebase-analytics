package com.outsystems.firebase.analytics.model

import org.json.JSONArray
import org.json.JSONObject

/**
 * Gets a JSONArray from a JSONObject
 * @param name the key to use
 * @return the JSONArray or an empty one if the key isn't present
 */
fun JSONObject.getArrayOrEmpty(name: String): JSONArray {
    return if(this.has(name)) { JSONArray(this.getString(name)) } else { JSONArray() }
}

/**
 * Gets a String from a JSONObject
 * @param name the key to use
 * @return the String or null if the key isn't present
 */
fun JSONObject.getStringOrNull(name: String): String? {
    return if(this.has(name)) { this.getString(name) } else { null }
}

