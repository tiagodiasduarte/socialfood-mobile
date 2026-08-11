package pt.socialfood.domain.usecase.user

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.fakes.FakeUsersRepository
import pt.socialfood.random.nextPresignedUrlData
import pt.socialfood.random.nextString
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class GetPresignedUrlUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then forwards userId fileName mimeType and context`() = runTest {
        // Given
        val presignedUrlData = Random.nextPresignedUrlData()
        val userId = Random.nextString()
        val fileName = Random.nextString()
        val mimeType = Random.nextString()
        val context = Random.nextString()
        val repository = FakeUsersRepository(getPresignedUrlResult = Result.Success(presignedUrlData))
        val useCase = GetPresignedUrlUseCaseImpl(repository)

        // When
        val result = useCase(userId = userId, fileName = fileName, mimeType = mimeType, context = context)

        // Then
        assertEquals(Result.Success(presignedUrlData), result)
        assertEquals(userId, repository.lastPresignedUrlUserId)
        assertEquals(fileName, repository.lastPresignedUrlFileName)
        assertEquals(mimeType, repository.lastPresignedUrlMimeType)
        assertEquals(context, repository.lastPresignedUrlContext)
    }

    @Test
    fun `given repository fails when invoked then returns Failure`() = runTest {
        // Given
        val repository = FakeUsersRepository(
            getPresignedUrlResult = Result.Failure(DataError.Network(Exception("test error"))),
        )
        val useCase = GetPresignedUrlUseCaseImpl(repository)

        // When
        val result = useCase(
            userId = Random.nextString(),
            fileName = Random.nextString(),
            mimeType = Random.nextString(),
            context = Random.nextString(),
        )

        // Then
        assertIs<Result.Failure>(result)
    }
}
