package pt.socialfood.data.network.extensions

import io.ktor.client.plugins.ResponseException
import pt.socialfood.data.network.ApiException
import pt.socialfood.domain.error.DataError

fun Throwable.toDataError(): DataError = when (this) {
    is ApiException -> DataError.Known(code = error, message = message, httpStatus = response.status.value)
    is ResponseException -> DataError.Unknown(message = response.status.description, httpStatus = response.status.value)
    else -> DataError.Network(this)
}
