package pt.socialfood.presentation.restaurant.search

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.fakes.FakePlacesRepository
import pt.socialfood.fakes.FakeRestaurantsRepository
import pt.socialfood.fakes.FakeSaveRecentSearchedPlaceUseCase
import pt.socialfood.fakes.FakeSettingsRepository
import pt.socialfood.random.nextPlace
import pt.socialfood.random.nextRestaurant
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
class SearchRestaurantsViewModelTest {
    @Test
    fun `given addByPlaceId and enrichment succeed when onAddRestaurant is called then RestaurantAdded is emitted`() =
        runTestWithMainDispatcher {
            // Given
            val place = Random.nextPlace()
            val expectedRestaurant = Random.nextRestaurant()
            val restaurantsRepository = FakeRestaurantsRepository(
                awaitEnrichedResult = Result.Success(expectedRestaurant),
            )
            val fakeSaveRecent = FakeSaveRecentSearchedPlaceUseCase()
            val vm = SearchRestaurantsViewModel(
                FakePlacesRepository(),
                restaurantsRepository,
                FakeSettingsRepository(),
                fakeSaveRecent,
            )
            assertFalse(vm.isImportingRestaurant.value)

            // When / Then
            vm.events.test {
                vm.onAddRestaurant(place)

                val event = awaitItem() as SearchRestaurantsViewModel.UiEvent.RestaurantAdded
                assertEquals(expectedRestaurant, event.restaurant)
            }

            assertEquals(1, restaurantsRepository.addByPlaceIdInvokeCount)
            assertEquals(1, restaurantsRepository.awaitEnrichedInvokeCount)
            assertEquals(1, fakeSaveRecent.invokeCount)
            assertEquals(place, fakeSaveRecent.lastPlace)
            assertFalse(vm.isImportingRestaurant.value)
        }

    @Test
    fun `given addByPlaceId fails when onAddRestaurant is called then no event emitted and enrichment never awaited`() =
        runTestWithMainDispatcher {
            // Given
            val restaurantsRepository = FakeRestaurantsRepository(
                addByPlaceIdResult = Result.Failure(DataError.Network(Exception("test error"))),
            )
            val vm = SearchRestaurantsViewModel(
                FakePlacesRepository(),
                restaurantsRepository,
                FakeSettingsRepository(),
                FakeSaveRecentSearchedPlaceUseCase(),
            )

            // When
            vm.onAddRestaurant(Random.nextPlace())
            advanceUntilIdle()

            // Then
            assertFalse(vm.isImportingRestaurant.value)
            assertEquals(0, restaurantsRepository.awaitEnrichedInvokeCount)
        }

    @Test
    fun `given enrichment wait times out when onAddRestaurant is called then no event is emitted and dialog closes`() =
        runTestWithMainDispatcher {
            // Given
            val restaurantsRepository = FakeRestaurantsRepository(
                awaitEnrichedResult = Result.Failure(DataError.Network(Exception("test error"))),
            )
            val vm = SearchRestaurantsViewModel(
                FakePlacesRepository(),
                restaurantsRepository,
                FakeSettingsRepository(),
                FakeSaveRecentSearchedPlaceUseCase(),
            )

            // When
            vm.onAddRestaurant(Random.nextPlace())
            advanceUntilIdle()

            // Then
            assertFalse(vm.isImportingRestaurant.value)
            assertEquals(1, restaurantsRepository.addByPlaceIdInvokeCount)
            assertEquals(1, restaurantsRepository.awaitEnrichedInvokeCount)
        }

    @Test
    fun `given an import already in flight when onAddRestaurant is called again then the second call is ignored`() =
        runTestWithMainDispatcher {
            // Given
            val restaurantsRepository = FakeRestaurantsRepository()
            val vm = SearchRestaurantsViewModel(
                FakePlacesRepository(),
                restaurantsRepository,
                FakeSettingsRepository(),
                FakeSaveRecentSearchedPlaceUseCase(),
            )

            // When
            vm.onAddRestaurant(Random.nextPlace())
            vm.onAddRestaurant(Random.nextPlace())
            advanceUntilIdle()

            // Then
            assertEquals(1, restaurantsRepository.addByPlaceIdInvokeCount)
        }

    @Test
    fun `given a blank query when onSearchQueryChange is called then state is Loaded with empty results`() =
        runTestWithMainDispatcher {
            // Given
            val vm = SearchRestaurantsViewModel(
                FakePlacesRepository(),
                FakeRestaurantsRepository(),
                FakeSettingsRepository(),
                FakeSaveRecentSearchedPlaceUseCase(),
            )

            // When
            vm.onSearchQueryChange("   ")

            // Then
            assertEquals(SearchRestaurantsUiState.Loaded(emptyList()), vm.state.value)
            assertEquals("   ", vm.searchQuery)
        }

    @Test
    fun `given a query shorter than 3 characters when onSearchQueryChange is called then search is not triggered`() =
        runTestWithMainDispatcher {
            // Given
            val placesRepository = FakePlacesRepository()
            val vm = SearchRestaurantsViewModel(
                placesRepository,
                FakeRestaurantsRepository(),
                FakeSettingsRepository(),
                FakeSaveRecentSearchedPlaceUseCase(),
            )

            // When
            vm.onSearchQueryChange("ab")
            advanceUntilIdle()

            // Then
            assertEquals(0, placesRepository.searchInvokeCount)
            assertEquals("ab", vm.searchQuery)
        }

    @Test
    fun `given the places search succeeds when onSearchQueryChange is called then state is Loaded with the results`() =
        runTestWithMainDispatcher {
            // Given
            val places = listOf(Random.nextPlace())
            val placesRepository = FakePlacesRepository(result = Result.Success(places))
            val vm = SearchRestaurantsViewModel(
                placesRepository,
                FakeRestaurantsRepository(),
                FakeSettingsRepository(),
                FakeSaveRecentSearchedPlaceUseCase(),
            )

            // When / Then
            vm.state.test {
                assertEquals(SearchRestaurantsUiState.Loaded(emptyList()), awaitItem())

                vm.onSearchQueryChange("pizza")

                assertEquals(SearchRestaurantsUiState.Loading, awaitItem())
                assertEquals(SearchRestaurantsUiState.Loaded(places), awaitItem())
            }
            assertEquals(1, placesRepository.searchInvokeCount)
        }

    @Test
    fun `given the places search fails when onSearchQueryChange is called then state is Error`() =
        runTestWithMainDispatcher {
            // Given
            val placesRepository = FakePlacesRepository(
                result = Result.Failure(DataError.Network(Exception("test error"))),
            )
            val vm = SearchRestaurantsViewModel(
                placesRepository,
                FakeRestaurantsRepository(),
                FakeSettingsRepository(),
                FakeSaveRecentSearchedPlaceUseCase(),
            )

            // When / Then
            vm.state.test {
                assertEquals(SearchRestaurantsUiState.Loaded(emptyList()), awaitItem())

                vm.onSearchQueryChange("pizza")

                assertEquals(SearchRestaurantsUiState.Loading, awaitItem())
                assertEquals(SearchRestaurantsUiState.Error(ErrorCode.NETWORK), awaitItem())
            }
        }
}
