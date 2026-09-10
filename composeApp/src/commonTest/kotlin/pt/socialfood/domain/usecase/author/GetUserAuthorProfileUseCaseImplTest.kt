package pt.socialfood.domain.usecase.author

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.fakes.FakeAuthorsRepository
import pt.socialfood.random.nextAuthorDetail
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class GetUserAuthorProfileUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then returns the current user author profile`() = runTest {
        // Given
        val author = Random.nextAuthorDetail()
        val repository = FakeAuthorsRepository(findUserAuthorProfileResult = Result.Success(author))
        val useCase = GetUserAuthorProfileUseCaseImpl(repository)

        // When
        val result = useCase()

        // Then
        assertEquals(Result.Success(author), result)
        assertEquals(1, repository.findUserAuthorProfileInvokeCount)
    }

    @Test
    fun `given repository fails when invoked then returns Failure`() = runTest {
        // Given
        val repository = FakeAuthorsRepository(
            findUserAuthorProfileResult = Result.Failure(DataError.Network(Exception("test error"))),
        )
        val useCase = GetUserAuthorProfileUseCaseImpl(repository)

        // When
        val result = useCase()

        // Then
        assertIs<Result.Failure>(result)
    }
}
