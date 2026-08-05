package pt.socialfood.mapper

import pt.socialfood.data.network.model.place.PlaceResponse
import pt.socialfood.domain.model.Place
import pt.socialfood.random.nextString
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class PlaceMapperTest {
    @Test
    fun `given a PlaceResponse when mapped then returns the equivalent list of Place`() {
        // Given
        val place1 = PlaceResponse.Place(
            id = Random.nextString(),
            name = Random.nextString(),
            address = Random.nextString(20),
        )
        val place2 = PlaceResponse.Place(
            id = Random.nextString(),
            name = Random.nextString(),
            address = Random.nextString(20),
        )
        val response = PlaceResponse(results = listOf(place1, place2))

        // When
        val result = response.toPlaces()

        // Then
        assertEquals(
            listOf(
                Place(id = place1.id, name = place1.name, address = place1.address),
                Place(id = place2.id, name = place2.name, address = place2.address),
            ),
            result,
        )
    }
}
