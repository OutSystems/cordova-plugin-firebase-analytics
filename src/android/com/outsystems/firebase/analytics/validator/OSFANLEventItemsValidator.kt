package com.outsystems.firebase.analytics.validator

import com.outsystems.firebase.analytics.model.OSFANLDefaultValues
import com.outsystems.firebase.analytics.model.OSFANLError
import com.outsystems.firebase.analytics.model.OSFANLInputDataFieldKey.CUSTOM_PARAMETERS
import com.outsystems.firebase.analytics.model.OSFANLInputDataFieldKey.ITEMS
import com.outsystems.firebase.analytics.model.OSFANLInputDataFieldKey.ITEM_ID
import com.outsystems.firebase.analytics.model.OSFANLInputDataFieldKey.ITEM_NAME
import com.outsystems.firebase.analytics.model.OSFANLInputDataFieldKey.KEY
import org.json.JSONArray

class OSFANLEventItemsValidator(private var minLimit: OSFANLMinimumRequired? = null) {

    fun validate(items: JSONArray) {

        // validate minimum limit
        minLimit?.let {
            when (it) {
                OSFANLMinimumRequired.AT_LEAST_ONE -> if (items.length() == 0) throw OSFANLError.missing(
                    ITEMS.json
                )

                OSFANLMinimumRequired.ONE -> if (items.length() != 1) throw OSFANLError.tooMany(
                    ITEMS.json,
                    1
                )
            }
        }

        // validate maximum limit
        if (items.length() >= OSFANLDefaultValues.eventItemsMaximum)
            throw OSFANLError.tooMany(ITEMS.json, OSFANLDefaultValues.eventItemsMaximum)

        val itemKeySet = mutableSetOf<String>()
        for (i in 0 until items.length()) {
            val item = items.getJSONObject(i)

            for (key in item.keys()) {

                if (key == CUSTOM_PARAMETERS.json) {
                    validateCustomParameters(itemKeySet, item.getJSONArray(key))
                    continue
                }

                // validate duplicate keys
                if (itemKeySet.contains(key))
                    throw OSFANLError.duplicateKeys()

                itemKeySet.add(key)
            }
        }

        // validate value / currency
        var hasId = false
        var hasName = false
        itemKeySet.forEach {
            hasId = hasId || it == ITEM_ID.json
            hasName = hasName || it == ITEM_NAME.json
        }

        // should have at least one of itemId / itemName
        if (!hasId && !hasName)
            throw OSFANLError.missingItemIdName()

    }

    private fun validateCustomParameters(
        itemKeySet: Set<String>,
        customParameters: JSONArray
    ) {
        // validate custom parameters max size
        if (customParameters.length() >= OSFANLDefaultValues.itemCustomParametersMaximum)
            throw OSFANLError.tooMany(
                CUSTOM_PARAMETERS.json,
                OSFANLDefaultValues.itemCustomParametersMaximum
            )

        // validate custom parameters content
        val itemsKeys: MutableSet<String> = mutableSetOf()
        for (k in 0 until customParameters.length()) {
            val parameter = customParameters.getJSONObject(k)
            val key = parameter.getString(KEY.json)

            // validate duplicate keys
            if (itemsKeys.contains(key) || itemKeySet.contains(key))
                throw OSFANLError.duplicateItemsIn(key)

            itemsKeys.add(key)
        }
    }

}