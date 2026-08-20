package pt.socialfood.presentation.restaurant.search

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.fakes.FakeAddRestaurantByPlaceIdUseCase
import pt.socialfood.fakes.FakeAwaitEnrichedRestaurantByPlaceIdUseCase
import pt.socialfood.fakes.FakeSearchPlacesUseCase
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
    @Suppress("MaxLineLength", "ktlint:standard:max-line-length")
    fun `given addByPlaceId and the enrichment wait both succeed when onAddRestaurant is called then dialog closes and RestaurantAdded is emitted`() =
        runTestWithMainDispatcher {
            // Given
            val expectedRestaurant = Random.nextRestaurant()
            val fakeAdd = FakeAddRestaurantByPlaceIdUseCase()
            val fakeAwait = FakeAwaitEnrichedRestaurantByPlaceIdUseCase(Result.Success(expectedRestaurant))
            val vm = SearchRestaurantsViewModel(FakeSearchPlacesUseCase(), fakeAwait, fakeAdd)
            assertFalse(vm.isImportingRestaurant.value)

            // When / Then
            vm.events.test {
                vm.onAddRestaurant("place-1")

                val event = awaitItem() as SearchRestaurantsViewModel.UiEvent.RestaurantAdded
                assertEquals(expectedRestaurant, event.restaurant)
            }

            assertEquals(1, fakeAdd.invokeCount)
            assertEquals(1, fakeAwait.invokeCount)
            assertFalse(vm.isImportingRestaurant.value)
        }

    @Test
    @Suppress("MaxLineLength", "ktlint:standard:max-line-length")
    fun `given addByPlaceId fails when onAddRestaurant is called then no RestaurantAdded event is emitted and dialog closes and it never waits for enrichment`() =
        runTestWithMainDispatcher {
            // Given
            val fakeAdd = FakeAddRestaurantByPlaceIdUseCase(
                Result.Failure(DataError.Network(Exception("test error"))),
            )
            val fakeAwait = FakeAwaitEnrichedRestaurantByPlaceIdUseCase(Result.Success(Random.nextRestaurant()))
            val vm = SearchRestaurantsViewModel(FakeSearchPlacesUseCase(), fakeAwait, fakeAdd)

            // When
            vm.onAddRestaurant("place-1")
            advanceUntilIdle()

            // Then
            assertFalse(vm.isImportingRestaurant.value)
            assertEquals(0, fakeAwait.invokeCount)
        }

    @Test
    @Suppress("MaxLineLength", "ktlint:standard:max-line-length")
    fun `given the enrichment wait times out when onAddRestaurant is called then no RestaurantAdded event is emitted and dialog closes`() =
        runTestWithMainDispatcher {
            // Given
            val fakeAdd = FakeAddRestaurantByPlaceIdUseCase()
            val fakeAwait = FakeAwaitEnrichedRestaurantByPlaceIdUseCase(
                Result.Failure(DataError.Network(Exception("test error"))),
            )
            val vm = SearchRestaurantsViewModel(FakeSearchPlacesUseCase(), fakeAwait, fakeAdd)

            // When
            vm.onAddRestaurant("place-1")
            advanceUntilIdle()

            // Then
            assertFalse(vm.isImportingRestaurant.value)
            assertEquals(1, fakeAdd.invokeCount)
            assertEquals(1, fakeAwait.invokeCount)
        }

    @Test
    fun `given an import already in flight when onAddRestaurant is called again then the second call is ignored`() =
        runTestWithMainDispatcher {
            // Given
            val fakeAdd = FakeAddRestaurantByPlaceIdUseCase()
            val fakeAwait = FakeAwaitEnrichedRestaurantByPlaceIdUseCase(Result.Success(Random.nextRestaurant()))
            val vm = SearchRestaurantsViewModel(FakeSearchPlacesUseCase(), fakeAwait, fakeAdd)

            // When
            vm.onAddRestaurant("place-1")
            vm.onAddRestaurant("place-2")
            advanceUntilIdle()

            // Then
            assertEquals(1, fakeAdd.invokeCount)
        }

    @Test
    fun `given a blank query when onSearchQueryChange is called then state is Loaded with empty results`() =
        runTestWithMainDispatcher {
            // Given
            val vm = SearchRestaurantsViewModel(
                FakeSearchPlacesUseCase(),
                FakeAwaitEnrichedRestaurantByPlaceIdUseCase(Result.Success(Random.nextRestaurant())),
                FakeAddRestaurantByPlaceIdUseCase(),
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
            val searchPlaces = FakeSearchPlacesUseCase()
            val vm = SearchRestaurantsViewModel(
                searchPlaces,
                FakeAwaitEnrichedRestaurantByPlaceIdUseCase(Result.Success(Random.nextRestaurant())),
                FakeAddRestaurantByPlaceIdUseCase(),
            )

            // When
            vm.onSearchQueryChange("ab")
            advanceUntilIdle()

            // Then
            assertEquals(0, searchPlaces.invokeCount)
            assertEquals("ab", vm.searchQuery)
        }

    @Test
    fun `given searchPlaces succeeds when onSearchQueryChange is called then state is Loaded with the results`() =
        runTestWithMainDispatcher {
            // Given
            val places = listOf(Random.nextPlace())
            val searchPlaces = FakeSearchPlacesUseCase(Result.Success(places))
            val vm = SearchRestaurantsViewModel(
                searchPlaces,
                FakeAwaitEnrichedRestaurantByPlaceIdUseCase(Result.Success(Random.nextRestaurant())),
                FakeAddRestaurantByPlaceIdUseCase(),
            )

            // When / Then
            vm.state.test {
                assertEquals(SearchRestaurantsUiState.Loaded(emptyList()), awaitItem())

                vm.onSearchQueryChange("pizza")

                assertEquals(SearchRestaurantsUiState.Loading, awaitItem())
                assertEquals(SearchRestaurantsUiState.Loaded(places), awaitItem())
            }
            assertEquals(1, searchPlaces.invokeCount)
        }

    @Test
    fun `given searchPlaces fails when onSearchQueryChange is called then state is Error`() =
        runTestWithMainDispatcher {
            // Given
            val searchPlaces = FakeSearchPlacesUseCase(Result.Failure(DataError.Network(Exception("test error"))))
            val vm = SearchRestaurantsViewModel(
                searchPlaces,
                FakeAwaitEnrichedRestaurantByPlaceIdUseCase(Result.Success(Random.nextRestaurant())),
                FakeAddRestaurantByPlaceIdUseCase(),
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
