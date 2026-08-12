package pt.socialfood.mapper

import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.author.AuthorResponse
import pt.socialfood.data.network.model.guide.GuideResponse
import pt.socialfood.data.network.model.restaurant.RestaurantResponse
import pt.socialfood.data.network.model.search.RestaurantSuggestionsResponse
import pt.socialfood.data.network.model.search.SearchResponse
import pt.socialfood.domain.model.Search
import pt.socialfood.random.nextEnum
import pt.socialfood.random.nextNullable
import pt.socialfood.random.nextString
import pt.socialfood.random.nextUrl
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SearchMapperTest {
    private fun randomAuthorResponse() = AuthorResponse(
        id = Random.nextString(),
        name = Random.nextString(),
        username = Random.nextString(),
        imageUrl = Random.nextNullable { nextUrl() },
    )

    private fun randomGuideResponse() = GuideResponse(
        id = Random.nextString(),
        name = Random.nextString(),
        description = Random.nextString(),
        visibility = Random.nextEnum(),
        author = randomAuthorResponse(),
        numberOfRestaurants = Random.nextInt(0, 50),
        imageUrl = Random.nextNullable { nextUrl() },
    )

    private fun randomRestaurantResponse(photoNames: List<String> = listOf(Random.nextString())) = RestaurantResponse(
        id = Random.nextString(),
        name = Random.nextString(),
        description = Random.nextNullable { nextString(20) },
        photoNames = photoNames,
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
    fun `given a SearchResponse when mapped then returns authors then guides then restaurants as Search results`() {
        // Given
        val authorResponse = randomAuthorResponse()
        val guideResponse = randomGuideResponse()
        val restaurantResponse = randomRestaurantResponse()
        val response = SearchResponse(
            authors = PagedResponse(items = listOf(authorResponse), page = 1, limit = 20, total = 1),
            guides = PagedResponse(items = listOf(guideResponse), page = 1, limit = 20, total = 1),
            restaurants = PagedResponse(items = listOf(restaurantResponse), page = 1, limit = 20, total = 1),
        )

        // When
        val result = response.toSearchResults()

        // Then
        assertEquals(
            listOf(
                Search.AuthorResult(authorResponse.toAuthor()),
                Search.GuideResult(guideResponse.toGuide()),
                Search.RestaurantResult(restaurantResponse.toRestaurant()),
            ),
            result,
        )
    }

    @Test
    fun `given a restaurant with no photos when mapped then the RestaurantResult has an empty photoNames list`() {
        // Given
        val restaurantResponse = randomRestaurantResponse(photoNames = emptyList())
        val response = SearchResponse(
            authors = PagedResponse(items = emptyList(), page = 1, limit = 20, total = 0),
            guides = PagedResponse(items = emptyList(), page = 1, limit = 20, total = 0),
            restaurants = PagedResponse(items = listOf(restaurantResponse), page = 1, limit = 20, total = 1),
        )

        // When
        val result = response.toSearchResults()

        // Then
        val restaurantResult = assertIs<Search.RestaurantResult>(result.single())
        assertEquals(emptyList(), restaurantResult.restaurant.photoNames)
    }

    @Test
    fun `given a RestaurantSuggestionsResponse when mapped then returns the mapped restaurants and generatedAt`() {
        // Given
        val restaurantResponse = randomRestaurantResponse()
        val generatedAt = Random.nextString()
        val response = RestaurantSuggestionsResponse(
            restaurants = listOf(restaurantResponse),
            generatedAt = generatedAt,
        )

        // When
        val result = response.toRestaurantSuggestions()

        // Then
        assertEquals(listOf(restaurantResponse.toRestaurant()), result.restaurants)
        assertEquals(generatedAt, result.generatedAt)
    }
}
