package pt.socialfood.data.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging

// Plain HTTP client used only by Coil's network fetcher for async image loading.
// No base URL, no auth header, no 401 -> sessionManager.clear() — image URLs are absolute
// URLs to other hosts (e.g. S3), and a broken/expired image URL must never clear the session.
class ImageHttpClient(private val isDebug: Boolean = true) {
    val client = HttpClient {

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    println("[Image] $message")
                }
            }
            level = if (isDebug) LogLevel.HEADERS else LogLevel.NONE
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 120_000
            connectTimeoutMillis = 30_000
            socketTimeoutMillis = 120_000
        }
    }
}
