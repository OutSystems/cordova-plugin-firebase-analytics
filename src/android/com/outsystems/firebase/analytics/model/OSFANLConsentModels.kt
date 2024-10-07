package com.outsystems.firebase.analytics.model

import com.google.firebase.analytics.FirebaseAnalytics

enum class ConsentType(val value: Int, val consentType: FirebaseAnalytics.ConsentType) {
    AD_PERSONALIZATION(1, FirebaseAnalytics.ConsentType.AD_PERSONALIZATION),
    AD_STORAGE(2, FirebaseAnalytics.ConsentType.AD_STORAGE),
    AD_USER_DATA(3, FirebaseAnalytics.ConsentType.AD_USER_DATA),
    ANALYTICS_STORAGE(4, FirebaseAnalytics.ConsentType.ANALYTICS_STORAGE);

    companion object {
        private val map = entries.associateBy(ConsentType::value)

        @JvmStatic
        fun fromInt(value: Int): FirebaseAnalytics.ConsentType? = map[value]?.consentType
    }
}

enum class ConsentStatus(val value: Int, val consentStatus: FirebaseAnalytics.ConsentStatus) {
    GRANTED(1, FirebaseAnalytics.ConsentStatus.GRANTED),
    DENIED(2, FirebaseAnalytics.ConsentStatus.DENIED);

    companion object {
        private val map = entries.associateBy(ConsentStatus::value)

        @JvmStatic
        fun fromInt(value: Int): FirebaseAnalytics.ConsentStatus? = map[value]?.consentStatus
    }
}