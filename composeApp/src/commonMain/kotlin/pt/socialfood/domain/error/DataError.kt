package pt.socialfood.domain.error

sealed class DataError {
    data class Known(val code: String, val message: String, val httpStatus: Int) : DataError()
    data class Unknown(val message: String?, val httpStatus: Int) : DataError()
    data class Network(val cause: Throwable) : DataError()
}

fun DataError.displayMessage(): String? = when (this) {
    is DataError.Known -> message
    is DataError.Unknown -> message
    is DataError.Network -> null
}

fun DataError.toThrowable(): Throwable = when (this) {
    is DataError.Known -> Exception("[$code] $message")
    is DataError.Unknown -> Exception("HTTP $httpStatus: ${message ?: "unknown error"}")
    is DataError.Network -> cause
}
