package com.outsystems.firebase.analytics.model

/**
 * Represents all supported analytics events keys
 * @property json the json value for each event key
 */
enum class OSFANLInputDataFieldKey(val json: String) {
    CUSTOM_PARAMETERS("custom_parameters"),
    CURRENCY("currency"),
    EVENT("event"),
    EVENT_PARAMETERS("eventParameters"),
    ITEM("item"),
    ITEM_ID("item_id"),
    ITEM_LIST_ID("item_list_id"),
    ITEM_LIST_NAME("item_list_name"),
    ITEM_NAME("item_name"),
    ITEMS("items"),
    KEY("key"),
    QUANTITY("quantity"),
    SHIPPING("shipping"),
    TAX("tax"),
    TRANSACTION_ID("transaction_id"),
    VALUE("value"),
    TYPE_NUMBER("number")
}