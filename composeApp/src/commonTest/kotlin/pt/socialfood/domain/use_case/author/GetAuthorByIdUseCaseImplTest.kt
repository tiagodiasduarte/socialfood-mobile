package pt.socialfood.domain.use_case.author

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.fakes.FakeAuthorsRepository
import pt.socialfood.random.nextAuthorDetail
import pt.socialfood.random.nextString
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class GetAuthorByIdUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then returns the author and forwards the id`() = runTest {
        // Given
        val author = Random.nextAuthorDetail()
        val repository = FakeAuthorsRepository(findAuthorByIdResult = Result.Success(author))
        val useCase = GetAuthorByIdUseCaseImpl(repository)

        // When
        val result = useCase(author.id)

        // Then
        assertEquals(Result.Success(author), result)
        assertEquals(author.id, repository.lastFindAuthorByIdId)
    }

    @Test
    fun `given repository fails when invoked then returns Failure`() = runTest {
        // Given
        val repository = FakeAuthorsRepository(
            findAuthorByIdResult = Result.Failure(DataError.Network(Exception("test error"))),
        )
        val useCase = GetAuthorByIdUseCaseImpl(repository)

        // When
        val result = useCase(Random.nextString())

        // Then
        assertIs<Result.Failure>(result)
    }
}
