package pt.socialfood.mapper

import pt.socialfood.data.api.PlacesApi.Companion.buildImageUrl
import pt.socialfood.data.network.model.restaurant.RestaurantResponse
import pt.socialfood.domain.model.Location
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.random.nextNullable
import pt.socialfood.random.nextString
import pt.socialfood.random.nextUrl
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class RestaurantMapperTest {
    @Test
    fun `given a RestaurantResponse when mapped then returns the equivalent Restaurant with built photo urls`() {
        // Given
        val response = RestaurantResponse(
            id = Random.nextString(),
            name = Random.nextString(),
            description = Random.nextNullable { nextString(20) },
            photoNames = listOf(Random.nextString(), Random.nextString()),
            city = Random.nextString(),
            country = Random.nextString(),
            countryCode = Random.nextString(2),
            postalCode = Random.nextNullable { nextString(6) },
            phoneNumber = Random.nextString(9),
            address = Random.nextString(20),
            rating = Random.nextDouble(0.0, 5.0),
            userRatingCount = Random.nextInt(0, 5_000),
            websiteUrl = Random.nextNullable { nextUrl() },
            location = RestaurantResponse.Location(
                latitude = Random.nextDouble(-90.0, 90.0),
                longitude = Random.nextDouble(-180.0, 180.0),
            ),
            regularOpeningHours = listOf(Random.nextString(12)),
        )

        // When
        val result = response.toRestaurant()

        // Then
        assertEquals(
            Restaurant(
                id = response.id,
                name = response.name,
                description = response.description,
                city = response.city,
                country = response.country,
                countryCode = response.countryCode,
                postalCode = response.postalCode,
                photoNames = response.photoNames.map { buildImageUrl(it) },
                address = response.address,
                rating = response.rating,
                userRatingCount = response.userRatingCount,
                websiteUrl = response.websiteUrl,
                phoneNumber = response.phoneNumber,
                regularOpeningHours = response.regularOpeningHours,
                location = Location(latitude = response.location.latitude, longitude = response.location.longitude),
            ),
            result,
        )
    }
}
