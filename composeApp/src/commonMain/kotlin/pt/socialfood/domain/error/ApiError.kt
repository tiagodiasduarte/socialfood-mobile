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
