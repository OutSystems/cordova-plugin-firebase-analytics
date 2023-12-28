package com.outsystems.firebase.analytics.validator

import android.os.Bundle
import com.outsystems.firebase.analytics.model.OSFANLError
import com.outsystems.firebase.analytics.model.OSFANLInputDataFieldKey
import com.outsystems.firebase.analytics.model.OSFANLInputDataFieldKey.CURRENCY
import com.outsystems.firebase.analytics.model.OSFANLInputDataFieldKey.EVENT_PARAMETERS
import com.outsystems.firebase.analytics.model.OSFANLInputDataFieldKey.KEY
import com.outsystems.firebase.analytics.model.OSFANLInputDataFieldKey.TYPE_NUMBER
import com.outsystems.firebase.analytics.model.OSFANLInputDataFieldKey.VALUE
import com.outsystems.firebase.analytics.model.putAny
import org.json.JSONArray

/**
 * Gets a String from a JSONObject
 * @property requiredKeys a list of keys which should be required
 * @property requireValueCurrency indicates if value and currency should be validated
 * @property numberKeys indicates the keys which should be validated as numbers
 * @property maxLimit indicates the maximum limit of parameters
 * @return the String or null if the key isn't present
 */
class OSFANLEventParameterValidator private constructor(
    private val requiredKeys: List<String>,
    private val requireValueCurrency: Boolean,
    private val numberKeys: List<String>,
    private var maxLimit: Int? = null
) {

    class Builder {
        private val requiredKeys = mutableListOf<String>()
        private val numberKeys = mutableListOf<String>()
        private var requireValueCurrency = false
        private var maxLimit: Int? = null

        fun required(vararg keys: OSFANLInputDataFieldKey) =
            apply { keys.forEach { requiredKeys.add(it.json) } }
        fun number(vararg keys: OSFANLInputDataFieldKey) =
            apply { keys.forEach { numberKeys.add(it.json) } }
        fun requireCurrencyValue() = apply { requireValueCurrency = true }
        fun max(limit: Int) = apply { this.maxLimit = limit }

        fun build() = OSFANLEventParameterValidator(
            requiredKeys,
            requireValueCurrency,
            numberKeys,
            maxLimit
        )
    }

    /**
     * Validates a JSONArray of parameters
     * @param input the array of parameters to validate
     * @return a bundle of parameters to send with the log event
     */
    fun validate(input: JSONArray): Bundle {

        // validate maximum limit
        maxLimit?.let {
            if (input.length() >= it) throw OSFANLError.tooMany(EVENT_PARAMETERS.json, it)
        }

        val result = Bundle()
        val parameterKeySet = mutableSetOf<String>()
        var hasValue = false
        var hasCurrency = false
        for (i in 0 until input.length()) {
            val parameter = input.getJSONObject(i)
            val key = parameter.getString(KEY.json)
            val value = parameter.getString(VALUE.json)

            // validate type, if needed
            if (numberKeys.contains(key) && value.toFloatOrNull() == null)
                throw OSFANLError.invalidType(key, TYPE_NUMBER.json)

            // validate duplicate keys
            if (parameterKeySet.contains(key))
                throw OSFANLError.duplicateKeys()

            // validate value type
            if (requireValueCurrency && key == VALUE.json && value.toFloatOrNull() == null)
                throw OSFANLError.invalidType(VALUE.json, TYPE_NUMBER.json)

            // search for value / currency
            hasValue = hasValue || key == VALUE.json
            hasCurrency = hasCurrency || key == CURRENCY.json

            parameterKeySet.add(key)
            result.putAny(key, value)
        }

        // validate value / currency
        // if value is present, currency is required
        if (requireValueCurrency && hasValue && !hasCurrency)
            throw OSFANLError.missing(CURRENCY.json)

        // validate required keys
        requiredKeys.forEach {
            if (!parameterKeySet.contains(it))
                throw OSFANLError.missing(it)
        }

        return result
    }

}