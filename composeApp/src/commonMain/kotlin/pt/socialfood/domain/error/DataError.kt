package pt.socialfood.domain.error

sealed class DataError {
    data class Known(val statusCode: Int, val errorCode: ErrorCode, val message: String) : DataError()
    data class Unknown(val statusCode: Int, val message: String?) : DataError()
    data class Network(val cause: Throwable) : DataError()
}

fun DataError.displayMessage(): String? = when (this) {
    is DataError.Known -> message
    is DataError.Unknown -> message
    is DataError.Network -> null
}

fun DataError.toThrowable(): Throwable = when (this) {
    is DataError.Known -> Exception("[$errorCode] $message")
    is DataError.Unknown -> Exception("HTTP $statusCode: ${message ?: "unknown error"}")
    is DataError.Network -> cause
}

enum class ErrorCode {
    // Auth
    USER_ALREADY_EXISTS,
    INVALID_CREDENTIALS,
    EMAIL_NOT_VERIFIED,
    EMAIL_ALREADY_VERIFIED,
    INVALID_CODE,
    CODE_EXPIRED,
    NO_PENDING_VERIFICATION,
    RESEND_TOO_SOON,
    INVALID_GOOGLE_TOKEN,

    // Resources
    USER_NOT_FOUND,
    GUIDE_NOT_FOUND,
    AUTHOR_NOT_FOUND,
    RESTAURANT_NOT_FOUND,
    RESTAURANT_NOT_IN_GUIDE,
    HOME_SECTION_NOT_FOUND,

    // Follow
    CANNOT_FOLLOW_SELF,
    ALREADY_FOLLOWING,
    NOT_FOLLOWING,

    // Favourites
    ALREADY_FAVOURITED,
    NOT_FAVOURITED,

    // Home sections
    ITEM_TYPE_MISMATCH,

    // Generic
    FORBIDDEN,
    INVALID_REQUEST,
    INTERNAL_ERROR,

    // User
    INVALID_SOCIAL_LINK,

    UNKNOWN,
    ;

    companion object {
        fun from(value: String): ErrorCode = entries.find { it.name == value } ?: UNKNOWN
    }
}

