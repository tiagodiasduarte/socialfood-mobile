package pt.socialfood.presentation.search

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.domain.model.Search
import pt.socialfood.fakes.FakeGetRestaurantSuggestionsUseCase
import pt.socialfood.fakes.FakeSearchUseCase
import pt.socialfood.random.nextRestaurantSuggestions
import pt.socialfood.random.nextSearch
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @Test
    fun `given a blank query when onSearchQueryChange is called then state is Loaded with empty results`() =
        runTestWithMainDispatcher {
            // Given
            val vm = SearchViewModel(FakeSearchUseCase(), FakeGetRestaurantSuggestionsUseCase())

            // When
            vm.onSearchQueryChange("   ")
            advanceUntilIdle()

            // Then
            assertEquals(SearchUiState.Loaded(emptyList()), vm.state.value)
            assertEquals("   ", vm.query.value)
        }

    @Test
    fun `given a query shorter than the minimum length when onSearchQueryChange is called then search is skipped`() =
        runTestWithMainDispatcher {
            // Given
            val search = FakeSearchUseCase()
            val vm = SearchViewModel(search, FakeGetRestaurantSuggestionsUseCase())

            // When
            vm.onSearchQueryChange("a")
            advanceUntilIdle()

            // Then
            assertEquals(0, search.invokeCount)
            assertEquals("a", vm.query.value)
        }

    @Test
    fun `given search succeeds when onSearchQueryChange is called then state is Loaded with the results`() =
        runTestWithMainDispatcher {
            // Given
            val results = listOf(Random.nextSearch())
            val search = FakeSearchUseCase(Result.Success(results))
            val vm = SearchViewModel(search, FakeGetRestaurantSuggestionsUseCase())

            // When / Then
            vm.state.test {
                assertEquals(SearchUiState.Loaded(emptyList()), awaitItem())

                vm.onSearchQueryChange("pizza")

                assertEquals(SearchUiState.Loading, awaitItem())
                assertEquals(SearchUiState.Loaded(results), awaitItem())
            }
            assertEquals(1, search.invokeCount)
        }

    @Test
    fun `given search fails when onSearchQueryChange is called then state is Error`() = runTestWithMainDispatcher {
        // Given
        val search = FakeSearchUseCase(Result.Failure(DataError.Network(Exception("test error"))))
        val vm = SearchViewModel(search, FakeGetRestaurantSuggestionsUseCase())

        // When / Then
        vm.state.test {
            assertEquals(SearchUiState.Loaded(emptyList()), awaitItem())

            vm.onSearchQueryChange("pizza")

            assertEquals(SearchUiState.Loading, awaitItem())
            assertEquals(SearchUiState.Error(ErrorCode.NETWORK), awaitItem())
        }
    }

    @Test
    fun `given a rapid query change when onSearchQueryChange is called then only the last query is searched`() =
        runTestWithMainDispatcher {
            // Given
            val results = listOf(Random.nextSearch())
            val search = FakeSearchUseCase(Result.Success(results))
            val vm = SearchViewModel(search, FakeGetRestaurantSuggestionsUseCase())

            // When
            vm.onSearchQueryChange("pi")
            vm.onSearchQueryChange("pizza")
            advanceUntilIdle()

            // Then
            assertEquals(1, search.invokeCount)
            assertEquals("pizza", vm.query.value)
        }

    @Test
    fun `given suggestions succeed when onFavoriteRestaurantsClick is called then state is Loaded with restaurants`() =
        runTestWithMainDispatcher {
            // Given
            val suggestions = Random.nextRestaurantSuggestions()
            val getRestaurantSuggestions = FakeGetRestaurantSuggestionsUseCase(Result.Success(suggestions))
            val vm = SearchViewModel(FakeSearchUseCase(), getRestaurantSuggestions)

            // When / Then
            vm.state.test {
                assertEquals(SearchUiState.Loaded(emptyList()), awaitItem())

                vm.onFavoriteRestaurantsClick()

                assertEquals(SearchUiState.Loading, awaitItem())
                assertEquals(
                    SearchUiState.Loaded(suggestions.restaurants.map { Search.RestaurantResult(it) }),
                    awaitItem(),
                )
                cancelAndIgnoreRemainingEvents()
            }
            advanceUntilIdle()
            assertEquals(true, vm.suggestionResultsRequested.value)
            assertEquals(1, getRestaurantSuggestions.invokeCount)
        }

    @Test
    fun `given suggestions fail when onFavoriteRestaurantsClick is called then state is Error`() =
        runTestWithMainDispatcher {
            // Given
            val getRestaurantSuggestions = FakeGetRestaurantSuggestionsUseCase(
                Result.Failure(DataError.Network(Exception("test error"))),
            )
            val vm = SearchViewModel(FakeSearchUseCase(), getRestaurantSuggestions)

            // When / Then
            vm.state.test {
                assertEquals(SearchUiState.Loaded(emptyList()), awaitItem())

                vm.onFavoriteRestaurantsClick()

                assertEquals(SearchUiState.Loading, awaitItem())
                assertEquals(SearchUiState.Error(ErrorCode.NETWORK), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
            advanceUntilIdle()
        }

    @Test
    fun `given suggestion results requested when onSearchQueryChange is called then the flag resets`() =
        runTestWithMainDispatcher {
            // Given
            val vm = SearchViewModel(FakeSearchUseCase(), FakeGetRestaurantSuggestionsUseCase())
            vm.onFavoriteRestaurantsClick()
            advanceUntilIdle()

            // When
            vm.onSearchQueryChange("pizza")

            // Then
            assertEquals(false, vm.suggestionResultsRequested.value)
        }
}
