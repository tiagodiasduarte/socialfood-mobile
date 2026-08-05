package pt.socialfood.domain.use_case.author

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.fakes.FakeAuthorsRepository
import pt.socialfood.random.nextPagedAuthors
import pt.socialfood.random.nextString
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FindAuthorsUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then forwards page limit and query`() = runTest {
        // Given
        val paged = Random.nextPagedAuthors()
        val page = Random.nextInt(1, 10)
        val limit = Random.nextInt(10, 50)
        val query = Random.nextString()
        val repository = FakeAuthorsRepository(findAuthorsResult = Result.Success(paged))
        val useCase = FindAuthorsUseCaseImpl(repository)

        // When
        val result = useCase(page = page, limit = limit, query = query)

        // Then
        assertEquals(Result.Success(paged), result)
        assertEquals(page, repository.lastFindAuthorsPage)
        assertEquals(limit, repository.lastFindAuthorsLimit)
        assertEquals(query, repository.lastFindAuthorsQuery)
    }

    @Test
    fun `given repository fails when invoked then returns Failure`() = runTest {
        // Given
        val repository = FakeAuthorsRepository(
            findAuthorsResult = Result.Failure(DataError.Network(Exception("test error"))),
        )
        val useCase = FindAuthorsUseCaseImpl(repository)

        // When
        val result = useCase(page = Random.nextInt(1, 10), limit = Random.nextInt(10, 50))

        // Then
        assertIs<Result.Failure>(result)
    }
}
