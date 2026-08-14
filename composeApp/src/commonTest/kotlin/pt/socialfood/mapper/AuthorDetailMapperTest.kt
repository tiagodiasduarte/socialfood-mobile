package pt.socialfood.mapper

import pt.socialfood.data.network.model.author.AuthorDetailResponse
import pt.socialfood.domain.model.AuthorDetail
import pt.socialfood.random.nextNullable
import pt.socialfood.random.nextString
import pt.socialfood.random.nextUrl
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthorDetailMapperTest {
    @Test
    fun `given an AuthorDetailResponse when mapped then returns the equivalent AuthorDetail`() {
        // Given
        val guideResponse = AuthorDetailResponse.GuideResponse(
            id = Random.nextString(),
            name = Random.nextString(),
            description = Random.nextString(),
            numberOfRestaurants = Random.nextInt(0, 50),
            imageUrl = Random.nextNullable { nextUrl() },
        )
        val response = AuthorDetailResponse(
            id = Random.nextString(),
            name = Random.nextString(),
            username = Random.nextString(),
            imageUrl = Random.nextNullable { nextUrl() },
            guidesCount = Random.nextInt(0, 100),
            followersCount = Random.nextInt(0, 10_000),
            followingCount = Random.nextInt(0, 10_000),
            facebookUrl = Random.nextNullable { nextUrl() },
            instagramUrl = Random.nextNullable { nextUrl() },
            youtubeUrl = Random.nextNullable { nextUrl() },
            guides = listOf(guideResponse),
        )

        // When
        val result = response.toAuthorDetail()

        // Then
        assertEquals(
            AuthorDetail(
                id = response.id,
                name = response.name,
                username = response.username,
                imageUrl = response.imageUrl,
                guidesCount = response.guidesCount,
                followersCount = response.followersCount,
                followingCount = response.followingCount,
                facebookUrl = response.facebookUrl,
                instagramUrl = response.instagramUrl,
                youtubeUrl = response.youtubeUrl,
                guides = listOf(
                    AuthorDetail.Guide(
                        id = guideResponse.id,
                        name = guideResponse.name,
                        description = guideResponse.description,
                        numberOfRestaurant = guideResponse.numberOfRestaurants,
                        imageUrl = guideResponse.imageUrl,
                    ),
                ),
            ),
            result,
        )
    }
}
