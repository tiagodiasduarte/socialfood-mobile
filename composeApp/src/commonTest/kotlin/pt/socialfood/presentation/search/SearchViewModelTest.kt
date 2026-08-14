package pt.socialfood.presentation.search

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.domain.model.Search
import pt.socialfood.fakes.FakeGetGuideSuggestionsUseCase
import pt.socialfood.fakes.FakeGetRestaurantSuggestionsUseCase
import pt.socialfood.fakes.FakeSearchUseCase
import pt.socialfood.random.nextGuideSuggestions
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
            val vm = SearchViewModel(
                FakeSearchUseCase(),
                FakeGetRestaurantSuggestionsUseCase(),
                FakeGetGuideSuggestionsUseCase(),
            )

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
            val vm = SearchViewModel(search, FakeGetRestaurantSuggestionsUseCase(), FakeGetGuideSuggestionsUseCase())

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
            val vm = SearchViewModel(search, FakeGetRestaurantSuggestionsUseCase(), FakeGetGuideSuggestionsUseCase())

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
        val vm = SearchViewModel(search, FakeGetRestaurantSuggestionsUseCase(), FakeGetGuideSuggestionsUseCase())

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
            val vm = SearchViewModel(search, FakeGetRestaurantSuggestionsUseCase(), FakeGetGuideSuggestionsUseCase())

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
            val vm = SearchViewModel(FakeSearchUseCase(), getRestaurantSuggestions, FakeGetGuideSuggestionsUseCase())

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
            val vm = SearchViewModel(FakeSearchUseCase(), getRestaurantSuggestions, FakeGetGuideSuggestionsUseCase())

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
            val vm = SearchViewModel(
                FakeSearchUseCase(),
                FakeGetRestaurantSuggestionsUseCase(),
                FakeGetGuideSuggestionsUseCase(),
            )
            vm.onFavoriteRestaurantsClick()
            advanceUntilIdle()

            // When
            vm.onSearchQueryChange("pizza")

            // Then
            assertEquals(false, vm.suggestionResultsRequested.value)
        }

    @Test
    fun `given suggestions succeed when onFavoriteGuidesClick is called then state is Loaded with guides`() =
        runTestWithMainDispatcher {
            // Given
            val suggestions = Random.nextGuideSuggestions()
            val getGuideSuggestions = FakeGetGuideSuggestionsUseCase(Result.Success(suggestions))
            val vm = SearchViewModel(FakeSearchUseCase(), FakeGetRestaurantSuggestionsUseCase(), getGuideSuggestions)

            // When / Then
            vm.state.test {
                assertEquals(SearchUiState.Loaded(emptyList()), awaitItem())

                vm.onFavoriteGuidesClick()

                assertEquals(SearchUiState.Loading, awaitItem())
                assertEquals(
                    SearchUiState.Loaded(suggestions.guides.map { Search.GuideResult(it) }),
                    awaitItem(),
                )
                cancelAndIgnoreRemainingEvents()
            }
            advanceUntilIdle()
            assertEquals(true, vm.suggestionResultsRequested.value)
            assertEquals(1, getGuideSuggestions.invokeCount)
        }

    @Test
    fun `given suggestions fail when onFavoriteGuidesClick is called then state is Error`() =
        runTestWithMainDispatcher {
            // Given
            val getGuideSuggestions = FakeGetGuideSuggestionsUseCase(
                Result.Failure(DataError.Network(Exception("test error"))),
            )
            val vm = SearchViewModel(FakeSearchUseCase(), FakeGetRestaurantSuggestionsUseCase(), getGuideSuggestions)

            // When / Then
            vm.state.test {
                assertEquals(SearchUiState.Loaded(emptyList()), awaitItem())

                vm.onFavoriteGuidesClick()

                assertEquals(SearchUiState.Loading, awaitItem())
                assertEquals(SearchUiState.Error(ErrorCode.NETWORK), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
            advanceUntilIdle()
        }

    @Test
    fun `given guide suggestions were requested when retrySuggestions is called then retries guide suggestions`() =
        runTestWithMainDispatcher {
            // Given
            val getGuideSuggestions = FakeGetGuideSuggestionsUseCase()
            val vm = SearchViewModel(FakeSearchUseCase(), FakeGetRestaurantSuggestionsUseCase(), getGuideSuggestions)
            vm.onFavoriteGuidesClick()
            advanceUntilIdle()

            // When
            vm.retrySuggestions()
            advanceUntilIdle()

            // Then
            assertEquals(2, getGuideSuggestions.invokeCount)
        }

    @Test
    fun `given restaurant suggestions were requested when retrySuggestions is called then retries restaurants`() =
        runTestWithMainDispatcher {
            // Given
            val getRestaurantSuggestions = FakeGetRestaurantSuggestionsUseCase()
            val vm = SearchViewModel(FakeSearchUseCase(), getRestaurantSuggestions, FakeGetGuideSuggestionsUseCase())
            vm.onFavoriteRestaurantsClick()
            advanceUntilIdle()

            // When
            vm.retrySuggestions()
            advanceUntilIdle()

            // Then
            assertEquals(2, getRestaurantSuggestions.invokeCount)
        }

    @Test
    fun `given onFavoriteRestaurantsClick is called then activeSuggestionSource is RESTAURANTS`() =
        runTestWithMainDispatcher {
            // Given
            val vm = SearchViewModel(
                FakeSearchUseCase(),
                FakeGetRestaurantSuggestionsUseCase(),
                FakeGetGuideSuggestionsUseCase(),
            )

            // When
            vm.onFavoriteRestaurantsClick()
            advanceUntilIdle()

            // Then
            assertEquals(SuggestionSource.RESTAURANTS, vm.activeSuggestionSource.value)
        }

    @Test
    fun `given guide suggestions requested when onFavoriteGuidesClick is called then source is GUIDES`() =
        runTestWithMainDispatcher {
            // Given
            val vm = SearchViewModel(
                FakeSearchUseCase(),
                FakeGetRestaurantSuggestionsUseCase(),
                FakeGetGuideSuggestionsUseCase(),
            )

            // When
            vm.onFavoriteGuidesClick()
            advanceUntilIdle()

            // Then
            assertEquals(SuggestionSource.GUIDES, vm.activeSuggestionSource.value)
        }

    @Test
    fun `given suggestions were requested when onClearSuggestions is called then state and flags reset`() =
        runTestWithMainDispatcher {
            // Given
            val suggestions = Random.nextRestaurantSuggestions()
            val vm = SearchViewModel(
                FakeSearchUseCase(),
                FakeGetRestaurantSuggestionsUseCase(Result.Success(suggestions)),
                FakeGetGuideSuggestionsUseCase(),
            )
            vm.onFavoriteRestaurantsClick()
            advanceUntilIdle()

            // When
            vm.onClearSuggestions()

            // Then
            assertEquals(SearchUiState.Loaded(emptyList()), vm.state.value)
            assertEquals(false, vm.suggestionResultsRequested.value)
            assertEquals(null, vm.activeSuggestionSource.value)
        }

    @Test
    fun `given suggestions were cleared when retrySuggestions is called then nothing happens`() =
        runTestWithMainDispatcher {
            // Given
            val getRestaurantSuggestions = FakeGetRestaurantSuggestionsUseCase()
            val vm = SearchViewModel(FakeSearchUseCase(), getRestaurantSuggestions, FakeGetGuideSuggestionsUseCase())
            vm.onFavoriteRestaurantsClick()
            advanceUntilIdle()
            vm.onClearSuggestions()

            // When
            vm.retrySuggestions()
            advanceUntilIdle()

            // Then
            assertEquals(1, getRestaurantSuggestions.invokeCount)
        }
}
