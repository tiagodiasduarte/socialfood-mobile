package pt.socialfood.data.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import pt.socialfood.data.network.model.ErrorResponse
import pt.socialfood.data.network.model.login.RefreshTokenRequest
import pt.socialfood.data.network.model.login.RefreshTokenResponse
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
            logger = KermitKtorLogger(TAG)
            level = if (isDebug) LogLevel.ALL else LogLevel.NONE
        }

        install(Auth) {
            bearer {
                sendWithoutRequest { true }

                loadTokens {
                    val access = sessionManager.accessToken
                    val refresh = sessionManager.refreshToken
                    if (access != null && refresh != null) {
                        BearerTokens(accessToken = access, refreshToken = refresh)
                    } else {
                        null
                    }
                }

                refreshTokens {
                    val refresh = sessionManager.refreshToken ?: return@refreshTokens null
                    try {
                        val response: RefreshTokenResponse = client.post("auth/refresh") {
                            markAsRefreshTokenRequest()
                            contentType(ContentType.Application.Json)
                            setBody(RefreshTokenRequest(refreshToken = refresh))
                        }.body()

                        sessionManager.saveTokens(
                            newAccessToken = response.token,
                            newRefreshToken = response.refreshToken,
                        )

                        BearerTokens(accessToken = response.token, refreshToken = response.refreshToken)
                    } catch (e: ClientRequestException) {
                        if (e.response.status == HttpStatusCode.Unauthorized ||
                            e.response.status == HttpStatusCode.Forbidden
                        ) {
                            sessionManager.clear()
                        }
                        null
                    } catch (@Suppress("SwallowedException") _: IOException) {
                        // Transient network failure while refreshing: fail this attempt without
                        // clearing the session, so the next request can retry the refresh.
                        null
                    }
                }
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
                if (!response.status.isSuccess()) {
                    val body = try {
                        response.body<ErrorResponse>()
                    } catch (@Suppress("TooGenericExceptionCaught") _: Exception) {
                        null
                    }

                    throw ApiException(
                        response = response,
                        errorCode = body?.let { ErrorCode.from(it.error) } ?: ErrorCode.UNKNOWN,
                        message = body?.message ?: response.status.description,
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
        private const val TAG = "KtorHttpClient"
        private const val REQUEST_TIMEOUT_MS = 15_000L
        private const val CONNECT_TIMEOUT_MS = 15_000L
        private const val SOCKET_TIMEOUT_MS = 15_000L
        private const val MAX_RETRIES = 3
        private const val HTTP_STATUS_CLASS_DIVISOR = 100
        private const val SERVER_ERROR_STATUS_CLASS = 5

        private fun HttpStatusCode.isServerError() = value / HTTP_STATUS_CLASS_DIVISOR == SERVER_ERROR_STATUS_CLASS
    }
}
