package pt.socialfood.domain.usecase.search

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.fakes.FakeSearchRepository
import pt.socialfood.random.nextRestaurantSuggestions
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class GetRestaurantSuggestionsUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then returns the suggestions`() = runTest {
        // Given
        val suggestions = Random.nextRestaurantSuggestions()
        val repository = FakeSearchRepository(restaurantSuggestionsResult = Result.Success(suggestions))
        val useCase = GetRestaurantSuggestionsUseCaseImpl(repository)

        // When
        val result = useCase()

        // Then
        assertEquals(Result.Success(suggestions), result)
    }

    @Test
    fun `given repository fails when invoked then returns Failure`() = runTest {
        // Given
        val repository = FakeSearchRepository(
            restaurantSuggestionsResult = Result.Failure(DataError.Network(Exception("test error"))),
        )
        val useCase = GetRestaurantSuggestionsUseCaseImpl(repository)

        // When
        val result = useCase()

        // Then
        assertIs<Result.Failure>(result)
    }
}
