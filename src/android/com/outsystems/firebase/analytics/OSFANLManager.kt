package com.outsystems.firebase.analytics;

import android.os.Bundle
import com.outsystems.firebase.analytics.model.OSFANLAnalyticsEvent
import com.outsystems.firebase.analytics.model.OSFANLError
import com.outsystems.firebase.analytics.model.OSFANLEventOutputModel
import com.outsystems.firebase.analytics.model.OSFANLInputDataFieldKey.EVENT
import com.outsystems.firebase.analytics.model.OSFANLInputDataFieldKey.EVENT_PARAMETERS
import com.outsystems.firebase.analytics.model.OSFANLInputDataFieldKey.ITEMS
import com.outsystems.firebase.analytics.model.OSFANLInputDataFieldKey.SHIPPING
import com.outsystems.firebase.analytics.model.OSFANLInputDataFieldKey.TAX
import com.outsystems.firebase.analytics.model.OSFANLInputDataFieldKey.TRANSACTION_ID
import com.outsystems.firebase.analytics.model.OSFANLInputDataFieldKey.VALUE
import com.outsystems.firebase.analytics.model.putAny
import com.outsystems.firebase.analytics.validator.OSFANLEventItemsValidator
import com.outsystems.firebase.analytics.validator.OSFANLEventParameterValidator
import com.outsystems.firebase.analytics.validator.OSFANLMinimumRequired.AT_LEAST_ONE
import com.outsystems.firebase.analytics.validator.OSFANLMinimumRequired.ONE
import org.json.JSONArray
import org.json.JSONObject

class OSFANLManager {

    @Throws(OSFANLError::class)
    fun buildOutputEventFromInputJSON(input: JSONObject): OSFANLEventOutputModel {

        val eventName = input.getString(EVENT.json)
        val parameters = JSONArray(input.getString(EVENT_PARAMETERS.json))
        val items = JSONArray(input.getString(ITEMS.json))

        val eventBuilderMethod = getEventBuilderMethod(eventName)
        val eventBundle = eventBuilderMethod(this, parameters, items)

        return OSFANLEventOutputModel(eventName, eventBundle)
    }

    private fun getEventBuilderMethod(
        eventName: String
    ): (OSFANLManager, JSONArray, JSONArray) -> Bundle {

        when (OSFANLAnalyticsEvent.fromValue(eventName)) {

            null -> throw OSFANLError.unexpected(eventName)

            OSFANLAnalyticsEvent.PURCHASE ->
                return OSFANLManager::buildBundleForPurchaseParametersEventType

            OSFANLAnalyticsEvent.REFUND ->
                return OSFANLManager::buildBundleForRefundParametersEventType

            OSFANLAnalyticsEvent.SELECT_ITEM ->
                return OSFANLManager::buildBundleForOneItemParametersEventType

            OSFANLAnalyticsEvent.SELECT_PROMOTION ->
                return OSFANLManager::buildBundleForSelectPromotionParametersEventType

            OSFANLAnalyticsEvent.VIEW_ITEM_LIST ->
                return OSFANLManager::buildBundleForViewItemListParametersEventType

            OSFANLAnalyticsEvent.VIEW_PROMOTION ->
                return OSFANLManager::buildBundleForOneItemParametersEventType

            else ->
                return OSFANLManager::buildBundleForValueCurrencyAndMultipleItemsEventType
        }
    }

    private fun buildBundleForValueCurrencyAndMultipleItemsEventType(
        parameters: JSONArray,
        items: JSONArray
    ): Bundle {
        return buildAndValidateParameterBundle(
            parameters,
            items,
            OSFANLEventParameterValidator.Builder()
                .requireCurrencyValue()
                .build(),
            OSFANLEventItemsValidator(AT_LEAST_ONE)
        )
    }

    private fun buildBundleForPurchaseParametersEventType(
        parameters: JSONArray,
        items: JSONArray
    ): Bundle {
        return buildAndValidateParameterBundle(
            parameters,
            items,
            OSFANLEventParameterValidator.Builder()
                .requireCurrencyValue()
                .required(TRANSACTION_ID)
                .number(SHIPPING, TAX)
                .build(),
            OSFANLEventItemsValidator(AT_LEAST_ONE)
        )
    }

    private fun buildBundleForRefundParametersEventType(
        parameters: JSONArray,
        items: JSONArray
    ): Bundle {
        return buildAndValidateParameterBundle(
            parameters,
            items,
            OSFANLEventParameterValidator.Builder()
                .requireCurrencyValue()
                .required(TRANSACTION_ID)
                .number(SHIPPING, TAX, VALUE)
                .build()
        )
    }

    private fun buildBundleForOneItemParametersEventType(
        parameters: JSONArray,
        items: JSONArray
    ): Bundle {
        return buildAndValidateParameterBundle(
            parameters,
            items,
            itemsValidator = OSFANLEventItemsValidator(ONE)
        )
    }

    private fun buildBundleForSelectPromotionParametersEventType(
        parameters: JSONArray,
        items: JSONArray
    ): Bundle {
        return buildAndValidateParameterBundle(parameters, items)
    }

    private fun buildBundleForViewItemListParametersEventType(
        parameters: JSONArray,
        items: JSONArray
    ): Bundle {
        return buildAndValidateParameterBundle(
            parameters,
            items,
            itemsValidator = OSFANLEventItemsValidator(AT_LEAST_ONE)
        )
    }

    private fun buildAndValidateParameterBundle(
        parameters: JSONArray,
        items: JSONArray,
        parametersValidator: OSFANLEventParameterValidator = OSFANLEventParameterValidator.Builder().build(),
        itemsValidator: OSFANLEventItemsValidator = OSFANLEventItemsValidator()
    ): Bundle {

        val bundle = Bundle()

        // validate parameters
        parametersValidator.validate(parameters)

        // validate items
        itemsValidator.validate(items)

        bundle.putAny(ITEMS.json, items)

        return bundle
    }

}