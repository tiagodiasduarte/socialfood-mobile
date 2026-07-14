package pt.socialfood.data.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging

// Plain HTTP client for direct S3 uploads via presigned URLs.
// No base URL, no auth header — S3 presigned URLs carry their own auth in the URL.
class S3HttpClient(private val isDebug: Boolean = true) {
    val client = HttpClient {

        expectSuccess = true

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    println("[S3] $message")
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
