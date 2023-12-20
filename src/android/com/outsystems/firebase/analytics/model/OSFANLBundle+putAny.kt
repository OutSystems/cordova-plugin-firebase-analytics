package com.outsystems.firebase.analytics.model

import android.os.Bundle

fun Bundle.putAny(key: String, value: Any) {
    when (value) {
        is String -> this.putString(key, value)
        is Int -> this.putInt(key, value)
        is Double -> this.putDouble(key, value)
        is Long -> this.putLong(key, value)
    }
}