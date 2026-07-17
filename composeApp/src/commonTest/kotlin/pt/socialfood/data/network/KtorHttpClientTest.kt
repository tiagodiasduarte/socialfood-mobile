package pt.socialfood.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import pt.socialfood.fakes.FakeSettingsRepository
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Verifies the URL resolution behaviour configured in [KtorHttpClient]'s `defaultRequest` block:
 * relative-path requests must resolve under the `/v1` base path. Instantiates the real
 * [KtorHttpClient] with an injected [MockEngine] so the resolved request URL can be asserted
 * against production code, without making a real network call.
 */
class KtorHttpClientTest {

    private fun createMockClient(): Pair<HttpClient, MutableList<String>> {
        val requestedUrls = mutableListOf<String>()
        val engine = MockEngine { request ->
            requestedUrls += request.url.toString()
            respond(
                content = "true",
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val client = KtorHttpClient(
            sessionManager = SessionManager(FakeSettingsRepository()),
            engine = engine
        ).client
        return client to requestedUrls
    }

    @Test
    fun `given a relative path request when sent through the client then the resolved URL is prefixed with v1`() =
        runTest {
            // Given
            val (client, requestedUrls) = createMockClient()

            // When
            client.get("guides")

            // Then
            assertEquals("https://api.socialfood.pt/v1/guides", requestedUrls.single())
        }

    @Test
    fun `given the restaurant import request when sent through the client then it resolves under v1`() =
        runTest {
            // Given
            val (client, requestedUrls) = createMockClient()

            // When
            client.post("admin/import/restaurants")

            // Then
            assertEquals("https://api.socialfood.pt/v1/admin/import/restaurants", requestedUrls.single())
        }
}
