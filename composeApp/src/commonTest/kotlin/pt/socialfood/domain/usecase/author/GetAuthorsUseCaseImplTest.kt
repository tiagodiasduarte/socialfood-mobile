package pt.socialfood.domain.usecase.author

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.fakes.FakeAuthorsRepository
import pt.socialfood.random.nextPagedAuthors
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class GetAuthorsUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then forwards page and limit without a query`() = runTest {
        // Given
        val paged = Random.nextPagedAuthors()
        val page = Random.nextInt(1, 10)
        val limit = Random.nextInt(10, 50)
        val repository = FakeAuthorsRepository(findAuthorsResult = Result.Success(paged))
        val useCase = GetAuthorsUseCaseImpl(repository)

        // When
        val result = useCase(page = page, limit = limit)

        // Then
        assertEquals(Result.Success(paged), result)
        assertEquals(page, repository.lastFindAuthorsPage)
        assertEquals(limit, repository.lastFindAuthorsLimit)
        assertNull(repository.lastFindAuthorsQuery)
    }

    @Test
    fun `given repository fails when invoked then returns Failure`() = runTest {
        // Given
        val repository = FakeAuthorsRepository(
            findAuthorsResult = Result.Failure(DataError.Network(Exception("test error"))),
        )
        val useCase = GetAuthorsUseCaseImpl(repository)

        // When
        val result = useCase(page = Random.nextInt(1, 10), limit = Random.nextInt(10, 50))

        // Then
        assertIs<Result.Failure>(result)
    }
}
