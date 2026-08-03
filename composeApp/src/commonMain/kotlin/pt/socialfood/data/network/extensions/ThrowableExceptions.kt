package pt.socialfood.data.network.extensions

import io.ktor.client.plugins.ResponseException
import pt.socialfood.data.network.ApiException
import pt.socialfood.domain.error.ApiError

fun Throwable.toApiError(): ApiError = when (this) {
    is ApiException -> ApiError.Known(code = error, message = message, httpStatus = response.status.value)
    is ResponseException -> ApiError.Unknown(message = response.status.description, httpStatus = response.status.value)
    else -> ApiError.Network(this)
}
