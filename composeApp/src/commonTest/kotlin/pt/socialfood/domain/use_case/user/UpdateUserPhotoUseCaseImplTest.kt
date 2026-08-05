package pt.socialfood.domain.use_case.user

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.fakes.FakeUsersRepository
import pt.socialfood.random.nextString
import pt.socialfood.random.nextUrl
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class UpdateUserPhotoUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then forwards id and imageUrl`() = runTest {
        // Given
        val id = Random.nextString()
        val imageUrl = Random.nextUrl()
        val repository = FakeUsersRepository(updatePhotoResult = Result.Success(true))
        val useCase = UpdateUserPhotoUseCaseImpl(repository)

        // When
        val result = useCase(id, imageUrl)

        // Then
        assertEquals(Result.Success(true), result)
        assertEquals(id, repository.lastUpdatePhotoId)
        assertEquals(imageUrl, repository.lastUpdatePhotoImageUrl)
        assertEquals(1, repository.updatePhotoInvokeCount)
    }

    @Test
    fun `given repository fails when invoked then returns Failure`() = runTest {
        // Given
        val repository = FakeUsersRepository(
            updatePhotoResult = Result.Failure(DataError.Network(Exception("test error"))),
        )
        val useCase = UpdateUserPhotoUseCaseImpl(repository)

        // When
        val result = useCase(Random.nextString(), Random.nextUrl())

        // Then
        assertIs<Result.Failure>(result)
    }
}
