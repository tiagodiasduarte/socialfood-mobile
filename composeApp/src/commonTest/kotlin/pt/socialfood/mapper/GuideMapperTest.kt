package pt.socialfood.mapper

import pt.socialfood.data.local.entity.GuideEntity
import pt.socialfood.data.network.model.author.AuthorDetailResponse
import pt.socialfood.data.network.model.author.AuthorResponse
import pt.socialfood.data.network.model.guide.GuideDetailResponse
import pt.socialfood.data.network.model.guide.GuideResponse
import pt.socialfood.data.network.model.restaurant.RestaurantResponse
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.AuthorDetail
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.random.nextEnum
import pt.socialfood.random.nextGuide
import pt.socialfood.random.nextNullable
import pt.socialfood.random.nextString
import pt.socialfood.random.nextUrl
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class GuideMapperTest {
    private fun randomAuthorResponse() = AuthorResponse(
        id = Random.nextString(),
        name = Random.nextString(),
        username = Random.nextString(),
        imageUrl = Random.nextNullable { nextUrl() },
    )

    private fun randomRestaurantResponse() = RestaurantResponse(
        id = Random.nextString(),
        name = Random.nextString(),
        description = Random.nextNullable { nextString(20) },
        photoNames = listOf(Random.nextString()),
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
        regularOpeningHours = null,
    )

    @Test
    fun `given a GuideResponse when mapped then returns the equivalent Guide`() {
        // Given
        val authorResponse = randomAuthorResponse()
        val response = GuideResponse(
            id = Random.nextString(),
            name = Random.nextString(),
            description = Random.nextString(),
            visibility = Random.nextEnum(),
            author = authorResponse,
            numberOfRestaurants = Random.nextInt(0, 50),
            imageUrl = Random.nextNullable { nextUrl() },
        )

        // When
        val result = response.toGuide()

        // Then
        assertEquals(
            Guide(
                id = response.id,
                name = response.name,
                description = response.description,
                visibility = response.visibility,
                author = authorResponse.toAuthor(),
                numberOfRestaurant = response.numberOfRestaurants,
                imageUrl = response.imageUrl,
            ),
            result,
        )
    }

    @Test
    fun `given an AuthorDetailResponse Guide when mapped then returns the equivalent AuthorDetail Guide`() {
        // Given
        val response = AuthorDetailResponse.GuideResponse(
            id = Random.nextString(),
            name = Random.nextString(),
            description = Random.nextString(),
            numberOfRestaurants = Random.nextInt(0, 50),
            imageUrl = Random.nextNullable { nextUrl() },
        )

        // When
        val result = response.toAuthorDetailGuide()

        // Then
        assertEquals(
            AuthorDetail.Guide(
                id = response.id,
                name = response.name,
                description = response.description,
                numberOfRestaurant = response.numberOfRestaurants,
                imageUrl = response.imageUrl,
            ),
            result,
        )
    }

    @Test
    fun `given a GuideDetailResponse when mapped then returns the equivalent Guide with its restaurants`() {
        // Given
        val authorResponse = randomAuthorResponse()
        val restaurantResponse = randomRestaurantResponse()
        val response = GuideDetailResponse(
            id = Random.nextString(),
            name = Random.nextString(),
            description = Random.nextString(),
            visibility = Random.nextEnum(),
            author = authorResponse,
            restaurants = listOf(restaurantResponse),
            imageUrl = Random.nextNullable { nextUrl() },
        )

        // When
        val result = response.toGuide()

        // Then
        assertEquals(
            Guide(
                id = response.id,
                name = response.name,
                description = response.description,
                visibility = response.visibility,
                author = authorResponse.toAuthor(),
                numberOfRestaurant = 1,
                restaurants = listOf(restaurantResponse.toRestaurant()),
                imageUrl = response.imageUrl,
            ),
            result,
        )
    }

    @Test
    fun `given a Guide when mapped to entity then returns the equivalent GuideEntity`() {
        // Given
        val guide = Random.nextGuide()
        val scope = Random.nextString()
        val position = Random.nextInt(0, 100)

        // When
        val result = guide.toGuideEntity(scope, position)

        // Then
        assertEquals(
            GuideEntity(
                id = guide.id,
                scope = scope,
                name = guide.name,
                description = guide.description,
                visibility = guide.visibility.name,
                authorId = guide.author.id,
                authorName = guide.author.name,
                authorUsername = guide.author.username,
                authorImageUrl = guide.author.imageUrl,
                numberOfRestaurant = guide.numberOfRestaurant,
                imageUrl = guide.imageUrl,
                position = position,
            ),
            result,
        )
    }

    @Test
    fun `given a GuideEntity when mapped then returns the equivalent Guide`() {
        // Given
        val entity = GuideEntity(
            id = Random.nextString(),
            scope = Random.nextString(),
            name = Random.nextString(),
            description = Random.nextString(),
            visibility = Random.nextEnum<GuideVisibility>().name,
            authorId = Random.nextString(),
            authorName = Random.nextString(),
            authorUsername = Random.nextString(),
            authorImageUrl = Random.nextNullable { nextUrl() },
            numberOfRestaurant = Random.nextInt(0, 50),
            imageUrl = Random.nextNullable { nextUrl() },
            position = Random.nextInt(0, 100),
        )

        // When
        val result = entity.toGuide()

        // Then
        assertEquals(
            Guide(
                id = entity.id,
                name = entity.name,
                description = entity.description,
                visibility = GuideVisibility.valueOf(entity.visibility),
                author = Author(
                    id = entity.authorId,
                    name = entity.authorName,
                    username = entity.authorUsername,
                    imageUrl = entity.authorImageUrl,
                ),
                numberOfRestaurant = entity.numberOfRestaurant,
                imageUrl = entity.imageUrl,
            ),
            result,
        )
    }
}
