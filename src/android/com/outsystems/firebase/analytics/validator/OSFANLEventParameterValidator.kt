package com.outsystems.firebase.analytics.validator

import com.outsystems.firebase.analytics.model.OSFANLError
import com.outsystems.firebase.analytics.model.OSFANLInputDataFieldKey
import com.outsystems.firebase.analytics.model.OSFANLInputDataFieldKey.CURRENCY
import com.outsystems.firebase.analytics.model.OSFANLInputDataFieldKey.EVENT_PARAMETERS
import com.outsystems.firebase.analytics.model.OSFANLInputDataFieldKey.KEY
import com.outsystems.firebase.analytics.model.OSFANLInputDataFieldKey.TYPE_NUMBER
import com.outsystems.firebase.analytics.model.OSFANLInputDataFieldKey.VALUE
import org.json.JSONArray

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

    fun validate(input: JSONArray) {

        // validate maximum limit
        maxLimit?.let {
            if (input.length() >= it) throw OSFANLError.tooMany(EVENT_PARAMETERS.json, it)
        }

        val parameterKeySet = mutableSetOf<String>()
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

            parameterKeySet.add(key)
        }

        // validate required keys
        requiredKeys.forEach {
            if (!parameterKeySet.contains(it))
                throw OSFANLError.missing(it)
        }

        // validate value / currency
        if (requireValueCurrency) {
            var hasValue = false
            var hasCurrency = false
            parameterKeySet.forEach {
                hasValue = hasValue || it == VALUE.json
                hasCurrency = hasCurrency || it == CURRENCY.json
            }
            // if value is present, currency is required
            if (hasValue && !hasCurrency)
                throw OSFANLError.missing(CURRENCY.json)
        }

    }

    /*
   private fun validateEventParameters(
       parameters: JSONArray,
       requiredParameters: Array<OSFANLInputDataFieldKey>,
   ) {

       for (i in 0 until parameters.length()) {
           val parameter = parameters.get(i) as JSONObject
           val key = parameter.getString(OSFANLInputDataFieldKey.KEY.json)
           val value = parameter.get(VALUE.json)
           constraints.forEach { it.bind(key, value) }
           bundle.putAny(key, value)
       }
       constraints.forEach { it.validate() }
   }

   private fun validateCustomParameters(customParameters: JSONArray) {
       // validate custom parameters max size
       if (customParameters.length() >= OSFANLDefaultValues.itemCustomParametersMaximum)
           throw OSFANLError.tooMany(
               OSFANLInputDataFieldKey.CUSTOM_PARAMETERS.json,
               OSFANLDefaultValues.itemCustomParametersMaximum
           )

       // validate custom parameters content
       val itemsKeys: MutableSet<String> = mutableSetOf()
       for (k in 0 until customParameters.length()) {
           val parameter = customParameters.getJSONObject(k)
           val key = parameter.getString(OSFANLInputDataFieldKey.KEY.json)
           if (itemsKeys.contains(key))
               throw OSFANLError.duplicateItemsIn(key)
           itemsKeys.add(key)
       }
   }

   private fun validateItems(
       items: JSONArray,
       constraints: Array<OSFANLConstraint>
   ) {

       // validate items size
       if (items.length() >= OSFANLDefaultValues.eventItemsMaximum)
           throw OSFANLError.tooMany(ITEMS.json, OSFANLDefaultValues.eventItemsMaximum)

       // validate items content
       for (i in 0 until items.length()) {
           val item = items.get(i) as JSONObject
           for (key in item.keys()) {

               if (key == OSFANLInputDataFieldKey.CUSTOM_PARAMETERS.json) {
                   validateCustomParameters(item.getJSONArray(key))
                   continue
               }

               val value = item.get(key)
               constraints.forEach { it.bind(key, value) }
           }
       }
       constraints.forEach { it.validate() }
   }

   fun defaultConstraints(): Array<OSFANLConstraint> {
       return arrayOf(
           OSFANLRequiredConstraint(),
       )
   }

   fun currencyValueConstraints(): Array<OSFANLConstraint> {
       return defaultConstraints() + arrayOf(
           OSFANLRequiredIfPresentConstraint(CURRENCY, VALUE)
       )
   }

   fun purchaseConstraints(): Array<OSFANLConstraint> {
       return defaultConstraints() + arrayOf(
           OSFANLRequiredIfPresentConstraint(CURRENCY, VALUE),
           OSFANLRequiredConstraint(setOf(TRANSACTION_ID)),
           OSFANLTypeDecimalConstraint(setOf(SHIPPING, TAX))
       )
   }

   fun itemsConstraints(): Array<OSFANLConstraint> {
       return arrayOf(
           OSFANLItemIdNameConstraint(),
       )
   }

*/

}