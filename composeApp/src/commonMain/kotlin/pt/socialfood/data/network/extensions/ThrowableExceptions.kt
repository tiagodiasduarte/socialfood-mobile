package pt.socialfood.data.network.extensions

import io.ktor.client.plugins.*
import io.ktor.http.*
import pt.socialfood.domain.error.ErrorEntity

fun Throwable.toErrorEntity(): ErrorEntity = when (this) {
    is ResponseException -> mapStatusCode(response.status)
    is HttpRequestTimeoutException -> ErrorEntity.Network.TIMEOUT
    else -> ErrorEntity.Unknown
}

private fun mapStatusCode(status: HttpStatusCode): ErrorEntity =
    when (status) {
        HttpStatusCode.Unauthorized -> ErrorEntity.Unauthorized
        HttpStatusCode.Forbidden -> ErrorEntity.Network.ACCESS_DENIED
        HttpStatusCode.NotFound -> ErrorEntity.Network.NOT_FOUND
        HttpStatusCode.RequestTimeout -> ErrorEntity.Network.TIMEOUT
        HttpStatusCode.InternalServerError -> ErrorEntity.Network.INTERNAL_SERVER_ERROR
        HttpStatusCode.ServiceUnavailable -> ErrorEntity.Network.SERVER_UNAVAILABLE
        else -> ErrorEntity.Network.UNKNOWN
    }