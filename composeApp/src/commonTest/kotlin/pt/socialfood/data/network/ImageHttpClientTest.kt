package pt.socialfood.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertNull

/**
 * Verifies the core safety invariant configured (by omission) in [ImageHttpClient]: image
 * requests must never carry the main API's Authorization header, since image URLs are absolute
 * URLs to other hosts (e.g. S3) and must never trigger a 401 -> sessionManager.clear() logout.
 * Instantiates the real [ImageHttpClient] with an injected [MockEngine] so the outgoing request
 * headers can be asserted against production code, without making a real network call.
 */
class ImageHttpClientTest {

    private fun createMockClient(): Pair<HttpClient, MutableList<String?>> {
        val authorizationHeaders = mutableListOf<String?>()
        val engine = MockEngine { request ->
            authorizationHeaders += request.headers[HttpHeaders.Authorization]
            respond(
                content = "true",
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val client = ImageHttpClient(engine = engine).client
        return client to authorizationHeaders
    }

    @Test
    fun `given an image request when sent through the client then no Authorization header is attached`() = runTest {
        // Given
        val (client, authorizationHeaders) = createMockClient()

        // When
        client.get("https://images.socialfood.pt/guide-cover.jpg")

        // Then
        assertNull(authorizationHeaders.single())
    }
}
