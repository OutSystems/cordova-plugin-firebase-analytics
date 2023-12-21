package com.outsystems.firebase.analytics.model

import org.json.JSONArray
import org.json.JSONObject

fun JSONObject.getArrayOrEmpty(name: String): JSONArray {
    return if(this.has(name)) { JSONArray(this.getString(name)) } else { JSONArray() }
}

fun JSONObject.getStringOrNull(name: String): String? {
    return if(this.has(name)) { this.getString(name) } else { null }
}

