package pt.socialfood.domain.use_case.restaurant

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.fakes.FakeRestaurantsRepository
import pt.socialfood.random.nextPagedRestaurants
import pt.socialfood.random.nextString
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FindRestaurantsUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then forwards page limit and query`() = runTest {
        // Given
        val paged = Random.nextPagedRestaurants()
        val page = Random.nextInt(1, 10)
        val limit = Random.nextInt(10, 50)
        val query = Random.nextString()
        val repository = FakeRestaurantsRepository(findRestaurantsResult = Result.Success(paged))
        val useCase = FindRestaurantsUseCaseImpl(repository)

        // When
        val result = useCase(page = page, limit = limit, query = query)

        // Then
        assertEquals(Result.Success(paged), result)
        assertEquals(page, repository.lastFindRestaurantsPage)
        assertEquals(limit, repository.lastFindRestaurantsLimit)
        assertEquals(query, repository.lastFindRestaurantsQuery)
    }

    @Test
    fun `given repository fails when invoked then returns Failure`() = runTest {
        // Given
        val repository = FakeRestaurantsRepository(
            findRestaurantsResult = Result.Failure(DataError.Network(Exception("test error"))),
        )
        val useCase = FindRestaurantsUseCaseImpl(repository)

        // When
        val result = useCase(page = Random.nextInt(1, 10), limit = Random.nextInt(10, 50))

        // Then
        assertIs<Result.Failure>(result)
    }
}
