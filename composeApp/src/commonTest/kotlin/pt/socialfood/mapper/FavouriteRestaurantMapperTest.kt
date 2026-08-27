package pt.socialfood.mapper

import pt.socialfood.data.local.entity.FavouriteRestaurantEntity
import pt.socialfood.data.local.entity.FavouriteSyncState
import pt.socialfood.domain.model.Location
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.random.nextEnum
import pt.socialfood.random.nextNullable
import pt.socialfood.random.nextRestaurant
import pt.socialfood.random.nextString
import pt.socialfood.random.nextUrl
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class FavouriteRestaurantMapperTest {
    private fun randomFavouriteRestaurantEntity() = FavouriteRestaurantEntity(
        restaurantId = Random.nextString(),
        name = Random.nextString(),
        description = Random.nextNullable { nextString(20) },
        city = Random.nextString(),
        country = Random.nextString(),
        countryCode = Random.nextString(2),
        postalCode = Random.nextNullable { nextString(6) },
        address = Random.nextString(20),
        rating = Random.nextDouble(0.0, 5.0),
        userRatingCount = Random.nextInt(0, 5_000),
        websiteUrl = Random.nextNullable { nextUrl() },
        phoneNumber = Random.nextString(9),
        imageUrl = Random.nextNullable { nextUrl() },
        latitude = Random.nextDouble(-90.0, 90.0),
        longitude = Random.nextDouble(-180.0, 180.0),
        favouritedAt = Random.nextLong(),
        syncState = Random.nextEnum<FavouriteSyncState>().name,
        position = Random.nextInt(),
    )

    @Test
    fun `given a FavouriteRestaurantEntity when mapped to Restaurant then returns the equivalent Restaurant`() {
        // Given
        val entity = randomFavouriteRestaurantEntity()

        // When
        val result = entity.toRestaurant()

        // Then
        assertEquals(
            Restaurant(
                id = entity.restaurantId,
                name = entity.name,
                description = entity.description,
                city = entity.city,
                country = entity.country,
                countryCode = entity.countryCode,
                postalCode = entity.postalCode,
                imagesUrl = listOfNotNull(entity.imageUrl),
                address = entity.address,
                rating = entity.rating,
                userRatingCount = entity.userRatingCount,
                websiteUrl = entity.websiteUrl,
                phoneNumber = entity.phoneNumber,
                location = Location(latitude = entity.latitude, longitude = entity.longitude),
            ),
            result,
        )
    }

    @Test
    fun `given a Restaurant when mapped to entity then returns the equivalent FavouriteRestaurantEntity`() {
        // Given
        val restaurant = Random.nextRestaurant()
        val favouritedAt = Random.nextLong()
        val syncState = Random.nextEnum<FavouriteSyncState>()
        val position = Random.nextInt()

        // When
        val result = restaurant.toFavouriteRestaurantEntity(favouritedAt, syncState, position)

        // Then
        assertEquals(
            FavouriteRestaurantEntity(
                restaurantId = restaurant.id,
                name = restaurant.name,
                description = restaurant.description,
                city = restaurant.city,
                country = restaurant.country,
                countryCode = restaurant.countryCode,
                postalCode = restaurant.postalCode,
                address = restaurant.address,
                rating = restaurant.rating,
                userRatingCount = restaurant.userRatingCount,
                websiteUrl = restaurant.websiteUrl,
                phoneNumber = restaurant.phoneNumber,
                imageUrl = restaurant.imagesUrl.firstOrNull(),
                latitude = restaurant.location.latitude,
                longitude = restaurant.location.longitude,
                favouritedAt = favouritedAt,
                syncState = syncState.name,
                position = position,
            ),
            result,
        )
    }
}
