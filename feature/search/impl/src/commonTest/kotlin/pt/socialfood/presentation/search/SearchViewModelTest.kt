package pt.socialfood.presentation.search

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.domain.model.Search
import pt.socialfood.fakes.FakeSaveRecentSearchUseCase
import pt.socialfood.fakes.FakeSearchRepository
import pt.socialfood.fakes.FakeSettingsRepository
import pt.socialfood.random.nextGuideSuggestions
import pt.socialfood.random.nextRecentSearch
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
                FakeSearchRepository(),
                FakeSettingsRepository(),
                FakeSaveRecentSearchUseCase(),
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
            val searchRepository = FakeSearchRepository()
            val vm = SearchViewModel(
                searchRepository,
                FakeSettingsRepository(),
                FakeSaveRecentSearchUseCase(),
            )

            // When
            vm.onSearchQueryChange("a")
            advanceUntilIdle()

            // Then
            assertEquals(0, searchRepository.searchInvokeCount)
            assertEquals("a", vm.query.value)
        }

    @Test
    fun `given search succeeds when onSearchQueryChange is called then state is Loaded with the results`() =
        runTestWithMainDispatcher {
            // Given
            val results = listOf(Random.nextSearch())
            val searchRepository = FakeSearchRepository(result = Result.Success(results))
            val vm = SearchViewModel(
                searchRepository,
                FakeSettingsRepository(),
                FakeSaveRecentSearchUseCase(),
            )

            // When / Then
            vm.state.test {
                assertEquals(SearchUiState.Loaded(emptyList()), awaitItem())

                vm.onSearchQueryChange("pizza")

                assertEquals(SearchUiState.Loading, awaitItem())
                assertEquals(SearchUiState.Loaded(results), awaitItem())
            }
            assertEquals(1, searchRepository.searchInvokeCount)
        }

    @Test
    fun `given search fails when onSearchQueryChange is called then state is Error`() = runTestWithMainDispatcher {
        // Given
        val searchRepository = FakeSearchRepository(result = Result.Failure(DataError.Network(Exception("test error"))))
        val vm = SearchViewModel(
            searchRepository,
            FakeSettingsRepository(),
            FakeSaveRecentSearchUseCase(),
        )

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
            val searchRepository = FakeSearchRepository(result = Result.Success(results))
            val vm = SearchViewModel(
                searchRepository,
                FakeSettingsRepository(),
                FakeSaveRecentSearchUseCase(),
            )

            // When
            vm.onSearchQueryChange("pi")
            vm.onSearchQueryChange("pizza")
            advanceUntilIdle()

            // Then
            assertEquals(1, searchRepository.searchInvokeCount)
            assertEquals("pizza", vm.query.value)
        }

    @Test
    fun `given suggestions succeed when onFavoriteRestaurantsClick is called then state is Loaded with restaurants`() =
        runTestWithMainDispatcher {
            // Given
            val suggestions = Random.nextRestaurantSuggestions()
            val searchRepository = FakeSearchRepository(restaurantSuggestionsResult = Result.Success(suggestions))
            val vm = SearchViewModel(
                searchRepository,
                FakeSettingsRepository(),
                FakeSaveRecentSearchUseCase(),
            )

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
            assertEquals(1, searchRepository.restaurantSuggestionsInvokeCount)
        }

    @Test
    fun `given suggestions fail when onFavoriteRestaurantsClick is called then state is Error`() =
        runTestWithMainDispatcher {
            // Given
            val searchRepository = FakeSearchRepository(
                restaurantSuggestionsResult = Result.Failure(DataError.Network(Exception("test error"))),
            )
            val vm = SearchViewModel(
                searchRepository,
                FakeSettingsRepository(),
                FakeSaveRecentSearchUseCase(),
            )

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
                FakeSearchRepository(),
                FakeSettingsRepository(),
                FakeSaveRecentSearchUseCase(),
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
            val searchRepository = FakeSearchRepository(guideSuggestionsResult = Result.Success(suggestions))
            val vm = SearchViewModel(
                searchRepository,
                FakeSettingsRepository(),
                FakeSaveRecentSearchUseCase(),
            )

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
            assertEquals(1, searchRepository.guideSuggestionsInvokeCount)
        }

    @Test
    fun `given suggestions fail when onFavoriteGuidesClick is called then state is Error`() =
        runTestWithMainDispatcher {
            // Given
            val searchRepository = FakeSearchRepository(
                guideSuggestionsResult = Result.Failure(DataError.Network(Exception("test error"))),
            )
            val vm = SearchViewModel(
                searchRepository,
                FakeSettingsRepository(),
                FakeSaveRecentSearchUseCase(),
            )

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
            val searchRepository = FakeSearchRepository()
            val vm = SearchViewModel(
                searchRepository,
                FakeSettingsRepository(),
                FakeSaveRecentSearchUseCase(),
            )
            vm.onFavoriteGuidesClick()
            advanceUntilIdle()

            // When
            vm.retrySuggestions()
            advanceUntilIdle()

            // Then
            assertEquals(2, searchRepository.guideSuggestionsInvokeCount)
        }

    @Test
    fun `given restaurant suggestions were requested when retrySuggestions is called then retries restaurants`() =
        runTestWithMainDispatcher {
            // Given
            val searchRepository = FakeSearchRepository()
            val vm = SearchViewModel(
                searchRepository,
                FakeSettingsRepository(),
                FakeSaveRecentSearchUseCase(),
            )
            vm.onFavoriteRestaurantsClick()
            advanceUntilIdle()

            // When
            vm.retrySuggestions()
            advanceUntilIdle()

            // Then
            assertEquals(2, searchRepository.restaurantSuggestionsInvokeCount)
        }

    @Test
    fun `given onFavoriteRestaurantsClick is called then activeSuggestionSource is RESTAURANTS`() =
        runTestWithMainDispatcher {
            // Given
            val vm = SearchViewModel(
                FakeSearchRepository(),
                FakeSettingsRepository(),
                FakeSaveRecentSearchUseCase(),
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
                FakeSearchRepository(),
                FakeSettingsRepository(),
                FakeSaveRecentSearchUseCase(),
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
                FakeSearchRepository(restaurantSuggestionsResult = Result.Success(suggestions)),
                FakeSettingsRepository(),
                FakeSaveRecentSearchUseCase(),
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
            val searchRepository = FakeSearchRepository()
            val vm = SearchViewModel(
                searchRepository,
                FakeSettingsRepository(),
                FakeSaveRecentSearchUseCase(),
            )
            vm.onFavoriteRestaurantsClick()
            advanceUntilIdle()
            vm.onClearSuggestions()

            // When
            vm.retrySuggestions()
            advanceUntilIdle()

            // Then
            assertEquals(1, searchRepository.restaurantSuggestionsInvokeCount)
        }

    @Test
    fun `given a result when onResultClick is called then it is saved as a recent search`() =
        runTestWithMainDispatcher {
            // Given
            val result = Random.nextSearch()
            val savedSearches = listOf(Random.nextRecentSearch())
            val saveRecentSearch = FakeSaveRecentSearchUseCase(savedSearches)
            val vm = SearchViewModel(
                FakeSearchRepository(),
                FakeSettingsRepository(),
                saveRecentSearch,
            )

            // When
            vm.onResultClick(result)
            advanceUntilIdle()

            // Then
            assertEquals(1, saveRecentSearch.invokeCount)
            assertEquals(savedSearches, vm.recentSearches.value)
        }

    @Test
    fun `given a recent search when onRecentSearchClick is called then it is bumped to the front`() =
        runTestWithMainDispatcher {
            // Given
            val recentSearch = Random.nextRecentSearch()
            val savedSearches = listOf(recentSearch)
            val saveRecentSearch = FakeSaveRecentSearchUseCase(savedSearches)
            val vm = SearchViewModel(
                FakeSearchRepository(),
                FakeSettingsRepository(),
                saveRecentSearch,
            )

            // When
            vm.onRecentSearchClick(recentSearch)
            advanceUntilIdle()

            // Then
            assertEquals(recentSearch, saveRecentSearch.lastSearch)
            assertEquals(savedSearches, vm.recentSearches.value)
        }

    @Test
    fun `given persisted recent searches when the view model is created then they are loaded`() =
        runTestWithMainDispatcher {
            // Given
            val recentSearches = listOf(Random.nextRecentSearch())

            // When
            val vm = SearchViewModel(
                FakeSearchRepository(),
                FakeSettingsRepository(recentSearches = recentSearches),
                FakeSaveRecentSearchUseCase(),
            )
            advanceUntilIdle()

            // Then
            assertEquals(recentSearches, vm.recentSearches.value)
        }
}
