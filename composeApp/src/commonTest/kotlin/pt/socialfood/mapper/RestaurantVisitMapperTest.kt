package pt.socialfood.mapper

import pt.socialfood.data.local.entity.RestaurantVisitEntity
import pt.socialfood.data.local.entity.RestaurantVisitSyncState
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.RestaurantVisitStatus
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.random.nextEnum
import pt.socialfood.random.nextNullable
import pt.socialfood.random.nextRestaurant
import pt.socialfood.random.nextString
import pt.socialfood.random.nextUrl
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class RestaurantVisitMapperTest {
    private fun randomRestaurantVisitEntity() = RestaurantVisitEntity(
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
        status = Random.nextEnum<VisitStatus>().name,
        recordedAt = Random.nextLong(),
        syncState = Random.nextEnum<RestaurantVisitSyncState>().name,
    )

    @Test
    fun `given a RestaurantVisitEntity when mapped to Restaurant then returns the equivalent Restaurant`() {
        // Given
        val entity = randomRestaurantVisitEntity()

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
                photoNames = listOfNotNull(entity.imageUrl),
                address = entity.address,
                rating = entity.rating,
                userRatingCount = entity.userRatingCount,
                websiteUrl = entity.websiteUrl,
                phoneNumber = entity.phoneNumber,
            ),
            result,
        )
    }

    @Test
    fun `given a RestaurantVisitEntity when mapped to RestaurantVisitStatus then wraps the restaurant with status`() {
        // Given
        val entity = randomRestaurantVisitEntity()

        // When
        val result = entity.toRestaurantVisitStatus()

        // Then
        assertEquals(
            RestaurantVisitStatus(
                restaurant = entity.toRestaurant(),
                status = VisitStatus.valueOf(entity.status),
                recordedAt = entity.recordedAt,
            ),
            result,
        )
    }

    @Test
    fun `given a Restaurant when mapped to entity then returns the equivalent RestaurantVisitEntity`() {
        // Given
        val restaurant = Random.nextRestaurant()
        val status = Random.nextEnum<VisitStatus>()
        val recordedAt = Random.nextLong()
        val syncState = Random.nextEnum<RestaurantVisitSyncState>()

        // When
        val result = restaurant.toRestaurantVisitEntity(status, recordedAt, syncState)

        // Then
        assertEquals(
            RestaurantVisitEntity(
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
                imageUrl = restaurant.photoNames.firstOrNull(),
                status = status.name,
                recordedAt = recordedAt,
                syncState = syncState.name,
            ),
            result,
        )
    }
}
