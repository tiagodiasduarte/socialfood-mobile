package pt.socialfood.data.repository

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.search.SearchResponse
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.model.Search
import pt.socialfood.fakes.FakeSearchApi
import pt.socialfood.random.nextString
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SearchRepositoryImplTest {

    @Test
    fun `given the api returns results when search is called then returns Success with the mapped results`() = runTest {
        // Given
        val response = SearchResponse(
            authors = PagedResponse(items = emptyList(), page = 1, limit = 20, total = 0),
            guides = PagedResponse(items = emptyList(), page = 1, limit = 20, total = 0),
            restaurants = PagedResponse(items = emptyList(), page = 1, limit = 20, total = 0),
        )
        val repo = SearchRepositoryImpl(FakeSearchApi(response = response))

        // When
        val result = repo.search(page = 1, limit = 20, query = Random.nextString())

        // Then
        assertIs<Result.Success<List<Search>>>(result)
        assertEquals(emptyList(), result.data)
    }

    @Test
    fun `given the api throws when search is called then returns Error Network`() = runTest {
        // Given
        val repo = SearchRepositoryImpl(FakeSearchApi(shouldThrow = true))

        // When
        val result = repo.search(page = 1, limit = 20)

        // Then
        assertIs<Result.Failure>(result)
        assertIs<DataError.Network>(result.error)
    }
}
