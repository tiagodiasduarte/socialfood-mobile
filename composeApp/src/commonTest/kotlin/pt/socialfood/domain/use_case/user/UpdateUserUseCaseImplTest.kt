package pt.socialfood.domain.use_case.user

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.fakes.FakeUsersRepository
import pt.socialfood.random.nextString
import pt.socialfood.random.nextUrl
import pt.socialfood.random.nextUser
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class UpdateUserUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then forwards all fields`() = runTest {
        // Given
        val user = Random.nextUser()
        val id = Random.nextString()
        val imageUrl = Random.nextUrl()
        val name = Random.nextString()
        val username = Random.nextString()
        val facebookUrl = Random.nextUrl()
        val instagramUrl = Random.nextUrl()
        val youtubeUrl = Random.nextUrl()
        val repository = FakeUsersRepository(updateResult = Result.Success(user))
        val useCase = UpdateUserUseCaseImpl(repository)

        // When
        val result = useCase(
            id = id,
            imageUrl = imageUrl,
            name = name,
            username = username,
            facebookUrl = facebookUrl,
            instagramUrl = instagramUrl,
            youtubeUrl = youtubeUrl,
        )

        // Then
        assertEquals(Result.Success(user), result)
        assertEquals(id, repository.lastUpdateId)
        assertEquals(imageUrl, repository.lastUpdateImageUrl)
        assertEquals(name, repository.lastUpdateName)
        assertEquals(username, repository.lastUpdateUsername)
        assertEquals(facebookUrl, repository.lastUpdateFacebookUrl)
        assertEquals(instagramUrl, repository.lastUpdateInstagramUrl)
        assertEquals(youtubeUrl, repository.lastUpdateYoutubeUrl)
    }

    @Test
    fun `given repository fails when invoked then returns Failure`() = runTest {
        // Given
        val repository = FakeUsersRepository(
            updateResult = Result.Failure(DataError.Network(Exception("test error"))),
        )
        val useCase = UpdateUserUseCaseImpl(repository)

        // When
        val result = useCase(id = Random.nextString())

        // Then
        assertIs<Result.Failure>(result)
    }
}
