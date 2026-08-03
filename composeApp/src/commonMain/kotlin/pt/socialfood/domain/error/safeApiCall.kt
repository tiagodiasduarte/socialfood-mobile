package pt.socialfood.domain.error

import io.ktor.client.plugins.ResponseException
import kotlinx.io.IOException
import pt.socialfood.core.Result
import pt.socialfood.data.network.extensions.toApiError

suspend fun <T> safeApiCall(call: suspend () -> T): Result<T> {
    return try {
        Result.Success(call())
    } catch (e: ResponseException) {
        Result.Failure(e.toApiError())
    } catch (e: IOException) {
        Result.Failure(e.toApiError())
    }
}
