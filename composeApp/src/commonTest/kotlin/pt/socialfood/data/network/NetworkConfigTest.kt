package pt.socialfood.data.network

import kotlin.test.Test
import kotlin.test.assertEquals

class NetworkConfigTest {

    @Test
    fun `given NetworkConfig when API_URL is read then it contains the v1 segment`() {
        // Given
        // NetworkConfig is a singleton object with no setup required

        // When
        val apiUrl = NetworkConfig.API_URL

        // Then
        assertEquals("https://api.socialfood.pt/v1", apiUrl)
    }
}
