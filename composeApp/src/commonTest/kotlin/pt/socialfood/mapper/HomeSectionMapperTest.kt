package pt.socialfood.mapper

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import pt.socialfood.data.local.entity.HomeSectionEntity
import pt.socialfood.data.network.model.author.AuthorResponse
import pt.socialfood.data.network.model.guide.GuideResponse
import pt.socialfood.data.network.model.home.HomeSectionItemResponse
import pt.socialfood.data.network.model.home.HomeSectionResponse
import pt.socialfood.data.network.model.restaurant.RestaurantResponse
import pt.socialfood.domain.model.HomeItemType
import pt.socialfood.domain.model.HomeSection
import pt.socialfood.domain.model.HomeSectionItem
import pt.socialfood.domain.model.HomeSectionType
import pt.socialfood.random.nextEnum
import pt.socialfood.random.nextString
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HomeSectionMapperTest {
    private fun randomRestaurantResponse() = RestaurantResponse(
        id = Random.nextString(),
        name = Random.nextString(),
        description = null,
        photoNames = listOf(Random.nextString()),
        city = Random.nextString(),
        country = Random.nextString(),
        countryCode = Random.nextString(2),
        postalCode = null,
        phoneNumber = Random.nextString(9),
        address = Random.nextString(20),
        rating = Random.nextDouble(0.0, 5.0),
        userRatingCount = Random.nextInt(0, 5_000),
        websiteUrl = null,
        location = RestaurantResponse.Location(0.0, 0.0),
        regularOpeningHours = null,
    )

    private fun randomGuideResponse() = GuideResponse(
        id = Random.nextString(),
        name = Random.nextString(),
        description = Random.nextString(),
        visibility = Random.nextEnum(),
        author = AuthorResponse(id = Random.nextString(), name = Random.nextString(), username = Random.nextString()),
        numberOfRestaurants = Random.nextInt(0, 50),
    )

    @Test
    fun `given a HomeSectionResponse when mapped then returns the equivalent HomeSection`() {
        // Given
        val type: HomeSectionType = Random.nextEnum()
        val itemResponse = HomeSectionItemResponse(
            id = Random.nextString(),
            sectionId = Random.nextString(),
            itemId = Random.nextString(),
            itemType = HomeItemType.RESTAURANT.name,
            position = Random.nextInt(0, 10),
        )
        val response = HomeSectionResponse(
            id = Random.nextString(),
            title = Random.nextString(),
            type = type.name,
            position = Random.nextInt(0, 10),
            isActive = Random.nextBoolean(),
            items = listOf(itemResponse),
        )

        // When
        val result = response.toHomeSection()

        // Then
        assertEquals(
            HomeSection(
                id = response.id,
                title = response.title,
                type = type,
                position = response.position,
                isActive = response.isActive,
                items = listOf(itemResponse.toHomeSectionItem()),
            ),
            result,
        )
    }

    @Test
    fun `given a HomeSectionResponse with an unknown type when mapped then falls back to RESTAURANT_LIST`() {
        // Given
        val response = HomeSectionResponse(
            id = Random.nextString(),
            title = Random.nextString(),
            type = "NOT_A_REAL_TYPE",
            position = Random.nextInt(0, 10),
            isActive = Random.nextBoolean(),
        )

        // When
        val result = response.toHomeSection()

        // Then
        assertEquals(HomeSectionType.RESTAURANT_LIST, result.type)
    }

    @Test
    fun `given a HomeSectionItemResponse with a restaurant when mapped then returns the equivalent item`() {
        // Given
        val restaurantResponse = randomRestaurantResponse()
        val itemType: HomeItemType = Random.nextEnum()
        val response = HomeSectionItemResponse(
            id = Random.nextString(),
            sectionId = Random.nextString(),
            itemId = Random.nextString(),
            itemType = itemType.name,
            position = Random.nextInt(0, 10),
            restaurant = restaurantResponse,
            guide = null,
        )

        // When
        val result = response.toHomeSectionItem()

        // Then
        assertEquals(
            HomeSectionItem(
                id = response.id,
                sectionId = response.sectionId,
                itemId = response.itemId,
                itemType = itemType,
                position = response.position,
                restaurant = restaurantResponse.toRestaurant(),
                guide = null,
            ),
            result,
        )
    }

    @Test
    fun `given a HomeSectionItemResponse with a guide when mapped then returns the equivalent item`() {
        // Given
        val guideResponse = randomGuideResponse()
        val response = HomeSectionItemResponse(
            id = Random.nextString(),
            itemType = HomeItemType.GUIDE.name,
            position = Random.nextInt(0, 10),
            guide = guideResponse,
        )

        // When
        val result = response.toHomeSectionItem()

        // Then
        assertEquals(guideResponse.toGuide(), result.guide)
        assertNull(result.restaurant)
    }

    @Test
    fun `given a HomeSectionItemResponse with an unknown itemType when mapped then falls back to RESTAURANT`() {
        // Given
        val response = HomeSectionItemResponse(
            id = Random.nextString(),
            itemType = "NOT_A_REAL_TYPE",
            position = Random.nextInt(0, 10),
        )

        // When
        val result = response.toHomeSectionItem()

        // Then
        assertEquals(HomeItemType.RESTAURANT, result.itemType)
    }

    @Test
    fun `given a HomeSectionResponse when mapped to entity then serializes its items to JSON`() {
        // Given
        val itemResponse = HomeSectionItemResponse(
            id = Random.nextString(),
            itemType = HomeItemType.RESTAURANT.name,
            position = Random.nextInt(0, 10),
        )
        val response = HomeSectionResponse(
            id = Random.nextString(),
            title = Random.nextString(),
            type = HomeSectionType.RESTAURANT_LIST.name,
            position = Random.nextInt(0, 10),
            isActive = Random.nextBoolean(),
            items = listOf(itemResponse),
        )

        // When
        val result = response.toHomeSectionEntity()

        // Then
        assertEquals(response.id, result.id)
        assertEquals(response.title, result.title)
        assertEquals(response.type, result.type)
        assertEquals(response.position, result.position)
        assertEquals(response.isActive, result.isActive)
        assertEquals(
            listOf(itemResponse),
            Json.decodeFromString(ListSerializer(HomeSectionItemResponse.serializer()), result.itemsJson),
        )
    }

    @Test
    fun `given a HomeSectionEntity when mapped then deserializes its items from JSON`() {
        // Given
        val itemResponse = HomeSectionItemResponse(
            id = Random.nextString(),
            itemType = HomeItemType.RESTAURANT.name,
            position = Random.nextInt(0, 10),
        )
        val entity = HomeSectionEntity(
            id = Random.nextString(),
            title = Random.nextString(),
            type = HomeSectionType.GUIDE_LIST.name,
            position = Random.nextInt(0, 10),
            isActive = Random.nextBoolean(),
            itemsJson = Json.encodeToString(ListSerializer(HomeSectionItemResponse.serializer()), listOf(itemResponse)),
        )

        // When
        val result = entity.toHomeSection()

        // Then
        assertEquals(
            HomeSection(
                id = entity.id,
                title = entity.title,
                type = HomeSectionType.GUIDE_LIST,
                position = entity.position,
                isActive = entity.isActive,
                items = listOf(itemResponse.toHomeSectionItem()),
            ),
            result,
        )
    }
}
