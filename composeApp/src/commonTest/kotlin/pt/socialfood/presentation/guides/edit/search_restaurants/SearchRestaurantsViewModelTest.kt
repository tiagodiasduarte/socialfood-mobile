package pt.socialfood.presentation.guides.edit.search_restaurants

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.core.Result
import pt.socialfood.domain.error.ErrorEntity
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.fakes.FakeAddRestaurantByPlaceIdUseCase
import pt.socialfood.fakes.FakeAwaitEnrichedRestaurantByPlaceIdUseCase
import pt.socialfood.fakes.FakeSearchPlacesUseCase
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
class SearchRestaurantsViewModelTest {

    private fun restaurant() = Restaurant(
        id = "r1",
        name = "Le Jardin",
        description = null,
        city = "Lisbon",
        country = "Portugal",
        countryCode = "PT",
        postalCode = null,
        photoNames = emptyList(),
        address = "",
        rating = 0.0,
        userRatingCount = 0,
        websiteUrl = null,
        phoneNumber = "",
    )

    @Test
    fun `given addByPlaceId and the enrichment wait both succeed when onAddRestaurant is called then dialog closes and RestaurantAdded is emitted`() =
        runTestWithMainDispatcher {
            // Given
            val fakeAdd = FakeAddRestaurantByPlaceIdUseCase()
            val fakeAwait = FakeAwaitEnrichedRestaurantByPlaceIdUseCase(Result.Success(restaurant()))
            val vm = SearchRestaurantsViewModel(FakeSearchPlacesUseCase(), fakeAwait, fakeAdd)
            assertFalse(vm.isImportingRestaurant.value)

            // When / Then
            vm.events.test {
                vm.onAddRestaurant("place-1")

                val event = awaitItem() as SearchRestaurantsViewModel.UiEvent.RestaurantAdded
                assertEquals("r1", event.restaurant.id)
            }

            assertEquals(1, fakeAdd.invokeCount)
            assertEquals(1, fakeAwait.invokeCount)
            assertFalse(vm.isImportingRestaurant.value)
        }

    @Test
    fun `given addByPlaceId fails when onAddRestaurant is called then no RestaurantAdded event is emitted, dialog closes, and it never waits for enrichment`() =
        runTestWithMainDispatcher {
            // Given
            val fakeAdd = FakeAddRestaurantByPlaceIdUseCase(result = Result.Error(ErrorEntity.Unknown))
            val fakeAwait = FakeAwaitEnrichedRestaurantByPlaceIdUseCase(Result.Success(restaurant()))
            val vm = SearchRestaurantsViewModel(FakeSearchPlacesUseCase(), fakeAwait, fakeAdd)

            // When
            vm.onAddRestaurant("place-1")
            advanceUntilIdle()

            // Then
            assertFalse(vm.isImportingRestaurant.value)
            assertEquals(0, fakeAwait.invokeCount)
        }

    @Test
    fun `given the enrichment wait times out when onAddRestaurant is called then no RestaurantAdded event is emitted and dialog closes`() =
        runTestWithMainDispatcher {
            // Given
            val fakeAdd = FakeAddRestaurantByPlaceIdUseCase()
            val fakeAwait = FakeAwaitEnrichedRestaurantByPlaceIdUseCase(Result.Error(ErrorEntity.Network.TIMEOUT))
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
            val fakeAwait = FakeAwaitEnrichedRestaurantByPlaceIdUseCase(Result.Success(restaurant()))
            val vm = SearchRestaurantsViewModel(FakeSearchPlacesUseCase(), fakeAwait, fakeAdd)

            // When
            vm.onAddRestaurant("place-1")
            vm.onAddRestaurant("place-2")
            advanceUntilIdle()

            // Then
            assertEquals(1, fakeAdd.invokeCount)
        }
}
