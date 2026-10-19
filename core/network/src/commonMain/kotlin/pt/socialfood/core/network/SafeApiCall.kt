package pt.socialfood.core.network

import io.ktor.client.plugins.ResponseException
import kotlinx.io.IOException
import pt.socialfood.core.common.Result
import pt.socialfood.core.network.extensions.toDataError

suspend fun <T> safeApiCall(call: suspend () -> T): Result<T> = try {
    Result.Success(call())
} catch (e: ResponseException) {
    Result.Failure(e.toDataError())
} catch (e: IOException) {
    Result.Failure(e.toDataError())
}
