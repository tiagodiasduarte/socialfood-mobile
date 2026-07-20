package pt.socialfood.presentation.guides.edit.search_restaurants

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.core.Result
import pt.socialfood.domain.error.ErrorEntity
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.use_case.restaurant.RestaurantEnrichmentPolling
import pt.socialfood.fakes.FakeAddRestaurantByPlaceIdUseCase
import pt.socialfood.fakes.FakeGetRestaurantByPlaceIdUseCase
import pt.socialfood.fakes.FakeSearchPlacesUseCase
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
class SearchRestaurantsViewModelTest {

    private fun restaurant(enriching: Boolean) = Restaurant(
        id = "r1",
        name = if (enriching) "" else "Le Jardin",
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
        enriching = enriching,
    )

    @Test
    fun `given addByPlaceId succeeds and the restaurant is ready immediately when onAddRestaurant is called then dialog closes and RestaurantAdded is emitted without polling`() =
        runTestWithMainDispatcher {
            // Given
            val fakeAdd = FakeAddRestaurantByPlaceIdUseCase()
            val fakeGetByPlaceId = FakeGetRestaurantByPlaceIdUseCase(listOf(Result.Success(restaurant(enriching = false))))
            val vm = SearchRestaurantsViewModel(FakeSearchPlacesUseCase(), fakeGetByPlaceId, fakeAdd)
            assertFalse(vm.isImportingRestaurant.value)

            // When / Then
            vm.events.test {
                vm.onAddRestaurant("place-1")

                val event = awaitItem() as SearchRestaurantsViewModel.UiEvent.RestaurantAdded
                assertEquals("r1", event.restaurant.id)
                assertFalse(event.restaurant.enriching)
            }

            assertEquals(1, fakeAdd.invokeCount)
            assertEquals(1, fakeGetByPlaceId.invokeCount)
            assertFalse(vm.isImportingRestaurant.value)
        }

    @Test
    fun `given the restaurant is still enriching when onAddRestaurant polls then it keeps polling until ready then emits RestaurantAdded`() =
        runTestWithMainDispatcher {
            // Given
            val fakeAdd = FakeAddRestaurantByPlaceIdUseCase()
            val fakeGetByPlaceId = FakeGetRestaurantByPlaceIdUseCase(
                listOf(
                    Result.Success(restaurant(enriching = true)),
                    Result.Success(restaurant(enriching = true)),
                    Result.Success(restaurant(enriching = false)),
                )
            )
            val vm = SearchRestaurantsViewModel(FakeSearchPlacesUseCase(), fakeGetByPlaceId, fakeAdd)

            // When / Then
            vm.events.test {
                vm.onAddRestaurant("place-1")

                val event = awaitItem() as SearchRestaurantsViewModel.UiEvent.RestaurantAdded
                assertEquals("r1", event.restaurant.id)
                assertFalse(event.restaurant.enriching)
            }

            assertEquals(1, fakeAdd.invokeCount)
            assertEquals(3, fakeGetByPlaceId.invokeCount)
            assertFalse(vm.isImportingRestaurant.value)
        }

    @Test
    fun `given addByPlaceId fails when onAddRestaurant is called then no RestaurantAdded event is emitted and dialog closes`() =
        runTestWithMainDispatcher {
            // Given
            val fakeAdd = FakeAddRestaurantByPlaceIdUseCase(result = Result.Error(ErrorEntity.Unknown))
            val fakeGetByPlaceId = FakeGetRestaurantByPlaceIdUseCase(listOf(Result.Success(restaurant(enriching = false))))
            val vm = SearchRestaurantsViewModel(FakeSearchPlacesUseCase(), fakeGetByPlaceId, fakeAdd)

            // When
            vm.onAddRestaurant("place-1")
            advanceUntilIdle()

            // Then
            assertFalse(vm.isImportingRestaurant.value)
            assertEquals(0, fakeGetByPlaceId.invokeCount)
        }

    @Test
    fun `given the restaurant never finishes enriching when onAddRestaurant polls up to the cap then no RestaurantAdded event is emitted and dialog closes`() =
        runTestWithMainDispatcher {
            // Given
            val fakeAdd = FakeAddRestaurantByPlaceIdUseCase()
            val fakeGetByPlaceId = FakeGetRestaurantByPlaceIdUseCase(listOf(Result.Success(restaurant(enriching = true))))
            val vm = SearchRestaurantsViewModel(FakeSearchPlacesUseCase(), fakeGetByPlaceId, fakeAdd)

            // When
            vm.onAddRestaurant("place-1")
            advanceUntilIdle()

            // Then
            assertFalse(vm.isImportingRestaurant.value)
            assertEquals(1, fakeAdd.invokeCount)
            assertEquals(RestaurantEnrichmentPolling.MAX_POLL_ATTEMPTS, fakeGetByPlaceId.invokeCount)
        }
}
