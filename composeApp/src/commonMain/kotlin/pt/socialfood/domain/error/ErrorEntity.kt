package pt.socialfood.domain.error

sealed class ApiError {
    data class Known(val code: String, val message: String, val httpStatus: Int) : ApiError()
    data class Unknown(val message: String?, val httpStatus: Int) : ApiError()
    data class Network(val cause: Throwable) : ApiError()
}

fun ApiError.displayMessage(): String? = when (this) {
    is ApiError.Known -> message
    is ApiError.Unknown -> message
    is ApiError.Network -> null
}

enum class ErrorCode(val code: String) {
    // Auth
    USER_ALREADY_EXISTS("USER_ALREADY_EXISTS"),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS"),
    EMAIL_NOT_VERIFIED("EMAIL_NOT_VERIFIED"),
    EMAIL_ALREADY_VERIFIED("EMAIL_ALREADY_VERIFIED"),
    INVALID_CODE("INVALID_CODE"),
    CODE_EXPIRED("CODE_EXPIRED"),
    NO_PENDING_VERIFICATION("NO_PENDING_VERIFICATION"),
    RESEND_TOO_SOON("RESEND_TOO_SOON"),
    INVALID_GOOGLE_TOKEN("INVALID_GOOGLE_TOKEN"),

    // Resources
    USER_NOT_FOUND("USER_NOT_FOUND"),
    GUIDE_NOT_FOUND("GUIDE_NOT_FOUND"),
    AUTHOR_NOT_FOUND("AUTHOR_NOT_FOUND"),
    RESTAURANT_NOT_FOUND("RESTAURANT_NOT_FOUND"),
    RESTAURANT_NOT_IN_GUIDE("RESTAURANT_NOT_IN_GUIDE"),
    HOME_SECTION_NOT_FOUND("HOME_SECTION_NOT_FOUND"),

    // Follow
    CANNOT_FOLLOW_SELF("CANNOT_FOLLOW_SELF"),
    ALREADY_FOLLOWING("ALREADY_FOLLOWING"),
    NOT_FOLLOWING("NOT_FOLLOWING"),

    // Favourites
    ALREADY_FAVOURITED("ALREADY_FAVOURITED"),
    NOT_FAVOURITED("NOT_FAVOURITED"),

    // Home sections
    ITEM_TYPE_MISMATCH("ITEM_TYPE_MISMATCH"),

    // Generic
    FORBIDDEN("FORBIDDEN"),
    INVALID_REQUEST("INVALID_REQUEST"),
    INTERNAL_ERROR("INTERNAL_ERROR"),

    // User
    INVALID_SOCIAL_LINK("INVALID_SOCIAL_LINK"),
}

sealed class ErrorEntity {
    sealed class Network : ErrorEntity() {
        object ACCESS_DENIED : Network()
        object NOT_FOUND : Network()
        object TIMEOUT : Network()
        object INTERNAL_SERVER_ERROR : Network()
        object SERVER_UNAVAILABLE : Network()
        object UNKNOWN : Network()
    }

    sealed class Validation : ErrorEntity() {
        object EmptyTitle : Validation()
        object EmptyDescription : Validation()
        object PublicGuideNeedsMoreRestaurants : Validation()
        object PublicGuideNeedsImage : Validation()
    }

    data class Api(val message: String) : ErrorEntity()

    object Unauthorized : ErrorEntity()
    object InvalidCredentials : ErrorEntity()
    object PasswordMismatch : ErrorEntity()
    object Unknown : ErrorEntity()
}
