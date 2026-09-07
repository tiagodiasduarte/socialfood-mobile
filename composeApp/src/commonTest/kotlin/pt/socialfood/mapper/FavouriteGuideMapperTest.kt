package pt.socialfood.mapper

import pt.socialfood.data.local.entity.FavouriteGuideEntity
import pt.socialfood.data.local.entity.FavouriteSyncState
import pt.socialfood.domain.model.Author
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

class FavouriteGuideMapperTest {
    private fun randomFavouriteGuideEntity() = FavouriteGuideEntity(
        guideId = Random.nextString(),
        name = Random.nextString(),
        description = Random.nextString(),
        visibility = Random.nextEnum<GuideVisibility>().name,
        authorId = Random.nextString(),
        authorName = Random.nextString(),
        authorUsername = Random.nextString(),
        authorImageUrl = Random.nextNullable { nextUrl() },
        numberOfRestaurant = Random.nextInt(0, 50),
        imageUrl = Random.nextNullable { nextUrl() },
        favouritedAt = Random.nextLong(),
        syncState = Random.nextEnum<FavouriteSyncState>().name,
        position = Random.nextInt(),
    )

    @Test
    fun `given a FavouriteGuideEntity when mapped to Guide then returns the equivalent Guide`() {
        // Given
        val entity = randomFavouriteGuideEntity()

        // When
        val result = entity.toGuide()

        // Then
        assertEquals(
            Guide(
                id = entity.guideId,
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

    @Test
    fun `given a Guide when mapped to entity then returns the equivalent FavouriteGuideEntity`() {
        // Given
        val guide = Random.nextGuide()
        val favouritedAt = Random.nextLong()
        val syncState = Random.nextEnum<FavouriteSyncState>()
        val position = Random.nextInt()

        // When
        val result = guide.toFavouriteGuideEntity(favouritedAt, syncState, position)

        // Then
        assertEquals(
            FavouriteGuideEntity(
                guideId = guide.id,
                name = guide.name,
                description = guide.description,
                visibility = guide.visibility.name,
                authorId = guide.author.id,
                authorName = guide.author.name,
                authorUsername = guide.author.username,
                authorImageUrl = guide.author.imageUrl,
                numberOfRestaurant = guide.numberOfRestaurant,
                imageUrl = guide.imageUrl,
                favouritedAt = favouritedAt,
                syncState = syncState.name,
                position = position,
            ),
            result,
        )
    }
}
