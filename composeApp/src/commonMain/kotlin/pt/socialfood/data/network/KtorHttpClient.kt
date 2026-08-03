package pt.socialfood.data.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.http.encodedPath
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import pt.socialfood.data.network.model.ErrorResponse
import pt.socialfood.domain.error.ErrorCode

class KtorHttpClient(
    private val sessionManager: SessionManager,
    private val isDebug: Boolean = true,
    engine: HttpClientEngine? = null,
) {

    private val config: HttpClientConfig<*>.() -> Unit = {

        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = NetworkConfig.HOST
                encodedPath = "/${NetworkConfig.API_VERSION}/"
            }
        }

        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                },
            )
        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    println("[HTTP] $message")
                }
            }
            level = if (isDebug) LogLevel.ALL else LogLevel.NONE
        }

        install(DefaultRequest) {
            sessionManager.token?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }

        install(HttpTimeout) {
            requestTimeoutMillis = REQUEST_TIMEOUT_MS
            connectTimeoutMillis = CONNECT_TIMEOUT_MS
            socketTimeoutMillis = SOCKET_TIMEOUT_MS
        }

        install(HttpRequestRetry) {
            maxRetries = MAX_RETRIES
            retryIf { _, response ->
                response.status.isServerError() || response.status == HttpStatusCode.RequestTimeout
            }
            exponentialDelay()
        }

        HttpResponseValidator {
            validateResponse { response ->
                if (response.status == HttpStatusCode.Unauthorized) {
                    sessionManager.clear()
                }

                if (!response.status.isSuccess()) {
                    val body = try {
                        response.body<ErrorResponse>()
                    } catch (@Suppress("TooGenericExceptionCaught") _: Exception) {
                        return@validateResponse
                    }

                    throw ApiException(
                        response = response,
                        errorCode = ErrorCode.from(body.error),
                        message = body.message,
                    )
                }
            }
        }
    }

    val client = if (engine != null) {
        HttpClient(engine, config)
    } else {
        HttpClient(config)
    }

    companion object {
        private const val REQUEST_TIMEOUT_MS = 15_000L
        private const val CONNECT_TIMEOUT_MS = 15_000L
        private const val SOCKET_TIMEOUT_MS = 15_000L
        private const val MAX_RETRIES = 3
        private const val HTTP_STATUS_CLASS_DIVISOR = 100
        private const val SERVER_ERROR_STATUS_CLASS = 5

        private fun HttpStatusCode.isServerError() = value / HTTP_STATUS_CLASS_DIVISOR == SERVER_ERROR_STATUS_CLASS
    }
}
