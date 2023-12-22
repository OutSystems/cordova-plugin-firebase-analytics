package com.outsystems.firebase.analytics.model

import android.os.Bundle

/**
 * Represents the output from the validation of an event to log
 * @property name the name of the event to log
 * @property parameters a bundle of parameters to log with the event.
 * This also contains the items and custom items as well.
 */
data class OSFANLEventOutputModel(
    val name: String,
    val parameters: Bundle
)
