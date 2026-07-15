package pt.socialfood.data

import kotlin.test.Test
import kotlin.test.assertEquals

class PlacesApiTest {

    @Test
    fun `given a photo name when buildImageUrl is called then the URL contains the v1 segment`() {
        // Given
        val photoName = "some-photo-ref"

        // When
        val url = PlacesApi.buildImageUrl(photoName)

        // Then
        assertEquals("https://api.socialfood.pt/v1/places/photo?ref=some-photo-ref", url)
    }
}
