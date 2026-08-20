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

class CoilHttpClientTest {

    private fun createMockClient(): Pair<HttpClient, MutableList<String?>> {
        val authorizationHeaders = mutableListOf<String?>()
        val engine = MockEngine { request ->
            authorizationHeaders += request.headers[HttpHeaders.Authorization]
            respond(
                content = "true",
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val client = CoilHttpClient(engine = engine).client
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
