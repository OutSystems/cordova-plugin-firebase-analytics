package com.outsystems.firebase.analytics.model

import android.os.Bundle
import org.json.JSONArray

/**
 * Adds a range of different types to a bundle. Accepted types are String, Int, Double and Long
 * @param key the key in which to store the value
 * @param value the value to store
 */
fun Bundle.putAny(key: String, value: Any) {
    when (value) {
        is String -> this.putString(key, value)
        is Int -> this.putInt(key, value)
        is Double -> this.putDouble(key, value)
        is Long -> this.putLong(key, value)
    }
}