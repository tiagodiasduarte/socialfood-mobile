package pt.socialfood.data.network

import io.ktor.client.HttpClient
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
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class KtorHttpClient(
    private val sessionManager: SessionManager,
    private val isDebug: Boolean = true
) {
    val client = HttpClient {

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
                }
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

        HttpResponseValidator {
            validateResponse { response ->
                if (response.status.value == 401) {
                    sessionManager.clear()
                }
            }

            handleResponseException { exception ->
                // Optional: handle network exceptions here
            }
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
            connectTimeoutMillis = 15_000
            socketTimeoutMillis = 15_000
        }
    }
}