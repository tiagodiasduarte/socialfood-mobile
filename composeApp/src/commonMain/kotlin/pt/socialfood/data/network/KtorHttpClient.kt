package pt.socialfood.data.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.encodedPath
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import pt.socialfood.data.network.model.ErrorResponse

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
            requestTimeoutMillis = 15_000
            connectTimeoutMillis = 15_000
            socketTimeoutMillis = 15_000
        }


        HttpResponseValidator {
            validateResponse { response ->
                if (response.status.value == 401) {
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
                        error = body.error,
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
}
