package pt.socialfood.data.network.client

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import pt.socialfood.data.network.KermitKtorLogger

// Plain HTTP client for direct S3 uploads via presigned URLs.
// No base URL, no auth header — S3 presigned URLs carry their own auth in the URL.
class S3HttpClient(private val isDebug: Boolean = true, engine: HttpClientEngine? = null) {

    private val config: HttpClientConfig<*>.() -> Unit = {

        expectSuccess = true

        install(Logging) {
            logger = KermitKtorLogger(TAG)
            level = if (isDebug) LogLevel.HEADERS else LogLevel.NONE
        }

        install(HttpTimeout) {
            requestTimeoutMillis = REQUEST_TIMEOUT_MS
            connectTimeoutMillis = CONNECT_TIMEOUT_MS
            socketTimeoutMillis = SOCKET_TIMEOUT_MS
        }
    }

    val client = if (engine != null) {
        HttpClient(engine, config)
    } else {
        HttpClient(config)
    }

    companion object {
        private const val TAG = "S3HttpClient"
        private const val REQUEST_TIMEOUT_MS = 120_000L
        private const val CONNECT_TIMEOUT_MS = 30_000L
        private const val SOCKET_TIMEOUT_MS = 120_000L
    }
}
