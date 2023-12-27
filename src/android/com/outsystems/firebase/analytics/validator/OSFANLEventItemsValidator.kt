package com.outsystems.firebase.analytics.validator

import android.os.Bundle
import com.outsystems.firebase.analytics.model.OSFANLDefaultValues
import com.outsystems.firebase.analytics.model.OSFANLError
import com.outsystems.firebase.analytics.model.OSFANLInputDataFieldKey.CUSTOM_PARAMETERS
import com.outsystems.firebase.analytics.model.OSFANLInputDataFieldKey.ITEMS
import com.outsystems.firebase.analytics.model.OSFANLInputDataFieldKey.ITEM_ID
import com.outsystems.firebase.analytics.model.OSFANLInputDataFieldKey.ITEM_NAME
import com.outsystems.firebase.analytics.model.OSFANLInputDataFieldKey.KEY
import com.outsystems.firebase.analytics.model.OSFANLInputDataFieldKey.VALUE
import com.outsystems.firebase.analytics.model.putAny
import org.json.JSONArray

/**
 * Responsible for validating the Event items
 * @property minLimit the minimum expected number of items
 */
class OSFANLEventItemsValidator(
    private var minLimit: OSFANLMinimumRequired = OSFANLMinimumRequired.NONE
) {

    /**
     * Validates a JSONArray of items
     * @param items the array of items to validate
     * @return a list of bundles, each representing a validated item
     */
    fun validate(items: JSONArray): List<Bundle> {

        var inputItems = items
        val result = mutableListOf<Bundle>()

        // validate minimum limit
        when (minLimit) {
            // no processing is needed
            OSFANLMinimumRequired.NONE -> if(inputItems.length() == 0) return result
            OSFANLMinimumRequired.AT_LEAST_ONE -> if (inputItems.length() == 0)
                throw OSFANLError.missing(ITEMS.json)
            OSFANLMinimumRequired.ONE -> if (inputItems.length() == 0)
                throw OSFANLError.tooMany(ITEMS.json, OSFANLDefaultValues.itemQuantity)
            else
                inputItems = JSONArray().apply { put(inputItems.getJSONObject(0)) }
        }

        // validate maximum limit
        if (inputItems.length() >= OSFANLDefaultValues.eventItemsMaximum)
            throw OSFANLError.tooMany(ITEMS.json, OSFANLDefaultValues.eventItemsMaximum)

        for (i in 0 until inputItems.length()) {
            val item = inputItems.getJSONObject(i)

            val itemKeySet = mutableSetOf<String>()
            var hasId = false
            var hasName = false

            val itemBundle = Bundle()
            for (key in item.keys()) {
                val value = item[key]

                hasId = hasId || key == ITEM_ID.json
                hasName = hasName || key == ITEM_NAME.json

                if (key == CUSTOM_PARAMETERS.json) {
                    validateCustomParameters(itemKeySet, item.getJSONArray(key))
                    continue
                }

                // validate duplicate keys
                if (itemKeySet.contains(key))
                    throw OSFANLError.duplicateKeys()

                itemKeySet.add(key)
                itemBundle.putAny(key, value)
            }

            result.add(itemBundle)

            // should have at least one of itemId / itemName
            if (!hasId && !hasName)
                throw OSFANLError.missingItemIdName()
        }

        return result
    }

    /**
     * Validates a JSONArray of custom items
     * @param itemKeySet a set of already processed keys
     * @param customParameters the array of custom parameters to validate
     * @return a list of bundles, each representing a validated item
     */
    private fun validateCustomParameters(
        itemKeySet: Set<String>,
        customParameters: JSONArray
    ): Bundle {

        // validate custom parameters max size
        if (customParameters.length() >= OSFANLDefaultValues.itemCustomParametersMaximum)
            throw OSFANLError.tooMany(
                CUSTOM_PARAMETERS.json,
                OSFANLDefaultValues.itemCustomParametersMaximum
            )

        // validate custom parameters content
        val result = Bundle()
        val itemsKeys: MutableSet<String> = mutableSetOf()
        for (k in 0 until customParameters.length()) {
            val parameter = customParameters.getJSONObject(k)
            val key = parameter.getString(KEY.json)
            val value = parameter.getString(VALUE.json)

            // validate duplicate keys
            if (itemsKeys.contains(key) || itemKeySet.contains(key))
                throw OSFANLError.duplicateItemsIn(key)

            itemsKeys.add(key)
            result.putAny(key, value)
        }
        return result
    }

}