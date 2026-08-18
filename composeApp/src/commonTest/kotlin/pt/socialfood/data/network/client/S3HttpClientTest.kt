package pt.socialfood.data.network.client

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.put
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Verifies the `expectSuccess` behaviour configured in [S3HttpClient]: instantiates the real
 * [S3HttpClient] with an injected [MockEngine] so production config can be asserted against,
 * without making a real network call.
 */
class S3HttpClientTest {

    @Test
    fun `given a successful response when a request is sent then no exception is thrown`() = runTest {
        // Given
        val engine = MockEngine { respond(content = "", status = HttpStatusCode.OK) }
        val client = S3HttpClient(engine = engine).client

        // When
        val response = client.put("https://s3.example.com/upload")

        // Then
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `given a non success response when a request is sent then it throws ResponseException`() = runTest {
        // Given
        val engine = MockEngine { respond(content = "", status = HttpStatusCode.Forbidden) }
        val client = S3HttpClient(engine = engine).client

        // When
        val exception = assertFailsWith<ResponseException> {
            client.put("https://s3.example.com/upload")
        }

        // Then
        assertEquals(HttpStatusCode.Forbidden, exception.response.status)
    }
}
