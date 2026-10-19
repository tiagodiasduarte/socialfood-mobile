package pt.socialfood.data.network.extensions

import io.ktor.client.plugins.ResponseException
import pt.socialfood.core.common.error.DataError
import pt.socialfood.data.network.ApiException

fun Throwable.toDataError(): DataError = when (this) {
    is ApiException -> DataError.Known(statusCode = response.status.value, errorCode = errorCode, message = message)
    is ResponseException -> DataError.Unknown(statusCode = response.status.value, message = response.status.description)
    else -> DataError.Network(this)
}
