package pt.socialfood.mapper

import pt.socialfood.data.local.entity.AuthorEntity
import pt.socialfood.data.network.model.author.AuthorResponse
import pt.socialfood.domain.model.Author
import pt.socialfood.random.nextAuthor
import pt.socialfood.random.nextNullable
import pt.socialfood.random.nextString
import pt.socialfood.random.nextUrl
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthorMapperTest {
    @Test
    fun `given an AuthorResponse when mapped then returns the equivalent Author`() {
        // Given
        val response = AuthorResponse(
            id = Random.nextString(),
            name = Random.nextString(),
            username = Random.nextString(),
            imageUrl = Random.nextNullable { nextUrl() },
        )

        // When
        val result = response.toAuthor()

        // Then
        assertEquals(
            Author(id = response.id, name = response.name, username = response.username, imageUrl = response.imageUrl),
            result,
        )
    }

    @Test
    fun `given an Author when mapped to entity then returns the equivalent AuthorEntity with the given position`() {
        // Given
        val author = Random.nextAuthor()
        val position = Random.nextInt(0, 100)

        // When
        val result = author.toAuthorEntity(position)

        // Then
        assertEquals(
            AuthorEntity(
                id = author.id,
                name = author.name,
                username = author.username,
                imageUrl = author.imageUrl,
                position = position,
            ),
            result,
        )
    }

    @Test
    fun `given an AuthorEntity when mapped then returns the equivalent Author`() {
        // Given
        val entity = AuthorEntity(
            id = Random.nextString(),
            name = Random.nextString(),
            username = Random.nextString(),
            imageUrl = Random.nextNullable { nextUrl() },
            position = Random.nextInt(0, 100),
        )

        // When
        val result = entity.toAuthor()

        // Then
        assertEquals(
            Author(id = entity.id, name = entity.name, username = entity.username, imageUrl = entity.imageUrl),
            result,
        )
    }
}
