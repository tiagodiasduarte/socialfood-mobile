package pt.socialfood.domain.usecase.search

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.fakes.FakeSearchRepository
import pt.socialfood.random.nextSearch
import pt.socialfood.random.nextString
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SearchUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then forwards args and returns the results`() = runTest {
        // Given
        val results = listOf(Random.nextSearch())
        val page = Random.nextInt(1, 10)
        val limit = Random.nextInt(10, 50)
        val query = Random.nextString()
        val repository = FakeSearchRepository(result = Result.Success(results))
        val useCase = SearchUseCaseImpl(repository)

        // When
        val result = useCase(page = page, limit = limit, query = query)

        // Then
        assertEquals(Result.Success(results), result)
        assertEquals(page, repository.lastPage)
        assertEquals(limit, repository.lastLimit)
        assertEquals(query, repository.lastQuery)
    }

    @Test
    fun `given repository fails when invoked then returns Failure`() = runTest {
        // Given
        val repository = FakeSearchRepository(
            result = Result.Failure(DataError.Network(Exception("test error"))),
        )
        val useCase = SearchUseCaseImpl(repository)

        // When
        val result = useCase(page = Random.nextInt(1, 10), limit = Random.nextInt(10, 50))

        // Then
        assertIs<Result.Failure>(result)
    }
}
