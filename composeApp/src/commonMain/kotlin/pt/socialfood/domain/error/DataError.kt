package pt.socialfood.domain.error

sealed class DataError {
    data class Known(val statusCode: Int, val errorCode: String, val message: String) : DataError()
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
