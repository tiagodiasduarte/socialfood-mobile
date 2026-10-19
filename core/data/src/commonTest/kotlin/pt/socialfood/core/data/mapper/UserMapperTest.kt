package pt.socialfood.core.data.mapper

import pt.socialfood.core.model.User
import pt.socialfood.core.model.UserRole
import pt.socialfood.core.network.model.user.UserResponse
import pt.socialfood.core.testing.random.nextEmail
import pt.socialfood.core.testing.random.nextNullable
import pt.socialfood.core.testing.random.nextString
import pt.socialfood.core.testing.random.nextUrl
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class UserMapperTest {
    @Test
    fun `given a UserResponse with a valid role when mapped then returns the equivalent User`() {
        // Given
        val response = UserResponse(
            id = Random.nextString(),
            email = Random.nextEmail(),
            name = Random.nextString(),
            username = Random.nextString(),
            imageUrl = Random.nextNullable { nextUrl() },
            role = "ADMIN",
            facebookUrl = Random.nextNullable { nextUrl() },
            instagramUrl = Random.nextNullable { nextUrl() },
            youtubeUrl = Random.nextNullable { nextUrl() },
            isVerified = Random.nextBoolean(),
        )

        // When
        val result = response.toUser()

        // Then
        assertEquals(
            User(
                id = response.id,
                email = response.email,
                name = response.name,
                username = response.username,
                role = UserRole.ADMIN,
                imageUrl = response.imageUrl,
                facebookUrl = response.facebookUrl,
                instagramUrl = response.instagramUrl,
                youtubeUrl = response.youtubeUrl,
                isVerified = response.isVerified,
            ),
            result,
        )
    }

    @Test
    fun `given a UserResponse with a lowercase role when mapped then parses it case-insensitively`() {
        // Given
        val response = UserResponse(
            id = Random.nextString(),
            email = Random.nextEmail(),
            name = Random.nextString(),
            username = Random.nextString(),
            role = "admin",
        )

        // When
        val result = response.toUser()

        // Then
        assertEquals(UserRole.ADMIN, result.role)
    }

    @Test
    fun `given a UserResponse with an unknown role when mapped then falls back to USER`() {
        // Given
        val response = UserResponse(
            id = Random.nextString(),
            email = Random.nextEmail(),
            name = Random.nextString(),
            username = Random.nextString(),
            role = "NOT_A_REAL_ROLE",
        )

        // When
        val result = response.toUser()

        // Then
        assertEquals(UserRole.USER, result.role)
    }
}
