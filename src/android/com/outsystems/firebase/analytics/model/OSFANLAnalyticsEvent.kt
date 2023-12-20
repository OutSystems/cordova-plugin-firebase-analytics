package com.outsystems.firebase.analytics.model

enum class OSFANLAnalyticsEvent(val value: String) {
    ADD_PAYMENT_INFO("add_payment_info"),
    ADD_SHIPPING_INFO("add_shipping_info"),
    ADD_TO_CART("add_to_cart"),
    ADD_TO_WISHLIST("add_to_wishlist"),
    BEGIN_CHECKOUT("begin_checkout"),
    PURCHASE("purchase"),
    REFUND("refund"),
    REMOVE_FROM_CART("remove_from_cart"),
    SELECT_ITEM("select_item"),
    SELECT_PROMOTION("select_promotion"),
    VIEW_CART("view_cart"),
    VIEW_ITEM("view_item"),
    VIEW_ITEM_LIST("view_item_list"),
    VIEW_PROMOTION("view_promotion");

    companion object {
        fun fromValue(value: String): OSFANLAnalyticsEvent? =
            values().find { it.value == value }
    }
}