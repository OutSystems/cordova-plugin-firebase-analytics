package com.outsystems.firebase.analytics.model

/**
 * Represents an error thrown by the plugin
 * @property code the error code
 * @property message the error message
 */
class OSFANLError private constructor(
    codeNumber: ErrorCodes,
    override val message: String
) : Exception() {

    val code: String

    init {
        code = "OS-PLUG-FANL-${codeNumber.code.toString().padStart(4, '0')}"
    }

    companion object {
        fun duplicateItemsIn(parameter: String): OSFANLError {
            return OSFANLError(
                ErrorCodes.DUPLICATE_ITEMS_IN,
                "Parameter '$parameter' contains duplicate items."
            )
        }

        fun duplicateKeys(): OSFANLError {
            return OSFANLError(
                ErrorCodes.DUPLICATE_KEYS,
                "The dictionary contains duplicate keys"
            )
        }

        fun invalidType(parameter: String, type: String): OSFANLError {
            return OSFANLError(
                ErrorCodes.INVALID_TYPE,
                "Parameter '$parameter' must be of type '$type'"
            )
        }

        fun logEcommerceEventInputArgumentsIssue(): OSFANLError {
            return OSFANLError(
                ErrorCodes.LOG_ECOMMERCE_EVENT_INPUT_ARGUMENTS_ISSUE,
                "There's an issue with the `logECommerceEvent` input arguments."
            )
        }

        fun missing(parameter: String): OSFANLError {
            return OSFANLError(
                ErrorCodes.MISSING,
                "Required parameter '$parameter' is missing."
            )
        }

        fun missingItemIdName(): OSFANLError {
            return OSFANLError(
                ErrorCodes.MISSING_ITEM_ID_NAME,
                "Item requires an ID or a Name."
            )
        }

        fun tooMany(parameter: String, limit: Int): OSFANLError {
            return OSFANLError(
                ErrorCodes.TOO_MANY,
                "Parameter '$parameter' must be set to a maximum number of $limit."
            )
        }

        fun unexpected(event: String): OSFANLError {
            return OSFANLError(
                ErrorCodes.UNEXPECTED,
                "Event '$event' is not valid."
            )
        }
    }

    private enum class ErrorCodes(val code: Int) {
        DUPLICATE_ITEMS_IN(1),
        DUPLICATE_KEYS(2),
        INVALID_TYPE(3),
        LOG_ECOMMERCE_EVENT_INPUT_ARGUMENTS_ISSUE(4),
        MISSING(5),
        MISSING_ITEM_ID_NAME(6),
        TOO_MANY(7),
        UNEXPECTED(8)
    }

}