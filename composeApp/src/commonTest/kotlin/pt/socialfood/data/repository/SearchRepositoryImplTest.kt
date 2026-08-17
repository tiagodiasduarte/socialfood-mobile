package pt.socialfood.data.repository

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.search.GuideSuggestionsResponse
import pt.socialfood.data.network.model.search.RestaurantSuggestionsResponse
import pt.socialfood.data.network.model.search.SearchResponse
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.model.Search
import pt.socialfood.fakes.FakeSearchApi
import pt.socialfood.mapper.toGuideSuggestions
import pt.socialfood.mapper.toRestaurantSuggestions
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

    @Test
    fun `given the api returns suggestions when getRestaurantSuggestions is called then returns mapped Success`() =
        runTest {
            // Given
            val response = RestaurantSuggestionsResponse(restaurants = emptyList(), generatedAt = Random.nextString())
            val repo = SearchRepositoryImpl(FakeSearchApi(restaurantSuggestionsResponse = response))

            // When
            val result = repo.getRestaurantSuggestions()

            // Then
            assertEquals(Result.Success(response.toRestaurantSuggestions()), result)
        }

    @Test
    fun `given the api throws when getRestaurantSuggestions is called then returns Error Network`() = runTest {
        // Given
        val repo = SearchRepositoryImpl(FakeSearchApi(shouldThrow = true))

        // When
        val result = repo.getRestaurantSuggestions()

        // Then
        assertIs<Result.Failure>(result)
        assertIs<DataError.Network>(result.error)
    }

    @Test
    fun `given the api returns suggestions when getGuideSuggestions is called then returns mapped Success`() = runTest {
        // Given
        val response = GuideSuggestionsResponse(guides = emptyList(), generatedAt = Random.nextString())
        val repo = SearchRepositoryImpl(FakeSearchApi(guideSuggestionsResponse = response))

        // When
        val result = repo.getGuideSuggestions()

        // Then
        assertEquals(Result.Success(response.toGuideSuggestions()), result)
    }

    @Test
    fun `given the api throws when getGuideSuggestions is called then returns Error Network`() = runTest {
        // Given
        val repo = SearchRepositoryImpl(FakeSearchApi(shouldThrow = true))

        // When
        val result = repo.getGuideSuggestions()

        // Then
        assertIs<Result.Failure>(result)
        assertIs<DataError.Network>(result.error)
    }
}
