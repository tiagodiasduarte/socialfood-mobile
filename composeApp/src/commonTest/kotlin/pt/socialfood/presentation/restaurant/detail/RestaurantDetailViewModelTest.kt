package pt.socialfood.presentation.restaurant.detail

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import pt.socialfood.core.Result
import pt.socialfood.domain.error.ErrorEntity
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.fakes.FakeGetRestaurantByIdUseCase
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RestaurantDetailViewModelTest {

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
    fun `given a restaurant that is not enriching when loaded then state is Loaded without polling`() =
        runTestWithMainDispatcher {
            // Given
            val fakeUseCase = FakeGetRestaurantByIdUseCase(listOf(Result.Success(restaurant(enriching = false))))
            val vm = RestaurantDetailViewModel(fakeUseCase, restaurantId = "r1")

            // When / Then
            vm.state.test {
                assertEquals(RestaurantDetailUiState.Loading, awaitItem())

                val loaded = awaitItem() as RestaurantDetailUiState.Loaded
                assertFalse(loaded.restaurant.enriching)
                assertFalse(loaded.enrichmentTimedOut)
            }
            assertEquals(1, fakeUseCase.invokeCount)
        }

    @Test
    fun `given a restaurant that is enriching when it becomes ready then polling stops and state updates`() =
        runTestWithMainDispatcher {
            // Given
            val fakeUseCase = FakeGetRestaurantByIdUseCase(
                listOf(
                    Result.Success(restaurant(enriching = true)),
                    Result.Success(restaurant(enriching = false)),
                )
            )
            val vm = RestaurantDetailViewModel(fakeUseCase, restaurantId = "r1")

            // When / Then
            vm.state.test {
                assertEquals(RestaurantDetailUiState.Loading, awaitItem())

                val pending = awaitItem() as RestaurantDetailUiState.Loaded
                assertTrue(pending.restaurant.enriching)

                val ready = awaitItem() as RestaurantDetailUiState.Loaded
                assertFalse(ready.restaurant.enriching)
                assertFalse(ready.enrichmentTimedOut)
            }
            assertEquals(2, fakeUseCase.invokeCount)
        }

    @Test
    fun `given a restaurant still enriching after max poll attempts when polling then state is marked timed out`() =
        runTestWithMainDispatcher {
            // Given — every call keeps returning enriching, never resolves
            val fakeUseCase = FakeGetRestaurantByIdUseCase(listOf(Result.Success(restaurant(enriching = true))))
            val vm = RestaurantDetailViewModel(fakeUseCase, restaurantId = "r1")

            // When / Then
            vm.state.test {
                assertEquals(RestaurantDetailUiState.Loading, awaitItem())

                val pending = awaitItem() as RestaurantDetailUiState.Loaded
                assertTrue(pending.restaurant.enriching)
                assertFalse(pending.enrichmentTimedOut)

                val timedOut = awaitItem() as RestaurantDetailUiState.Loaded
                assertTrue(timedOut.restaurant.enriching)
                assertTrue(timedOut.enrichmentTimedOut)
            }
            // 1 initial load + one call per poll attempt
            assertEquals(1 + ENRICHMENT_POLL_MAX_ATTEMPTS, fakeUseCase.invokeCount)
        }

    @Test
    fun `given the restaurant fails to load when load is called then state is Error`() =
        runTestWithMainDispatcher {
            // Given
            val fakeUseCase = FakeGetRestaurantByIdUseCase(listOf(Result.Error(ErrorEntity.Unknown)))
            val vm = RestaurantDetailViewModel(fakeUseCase, restaurantId = "r1")

            // When / Then
            vm.state.test {
                assertEquals(RestaurantDetailUiState.Loading, awaitItem())
                assertEquals(RestaurantDetailUiState.Error, awaitItem())
            }
        }

    @Test
    fun `given polling in progress when an error occurs mid-poll then state is marked timed out instead of stuck`() =
        runTestWithMainDispatcher {
            // Given
            val fakeUseCase = FakeGetRestaurantByIdUseCase(
                listOf(
                    Result.Success(restaurant(enriching = true)),
                    Result.Error(ErrorEntity.Unknown),
                )
            )
            val vm = RestaurantDetailViewModel(fakeUseCase, restaurantId = "r1")

            // When / Then
            vm.state.test {
                assertEquals(RestaurantDetailUiState.Loading, awaitItem())

                val pending = awaitItem() as RestaurantDetailUiState.Loaded
                assertTrue(pending.restaurant.enriching)
                assertFalse(pending.enrichmentTimedOut)

                val timedOut = awaitItem() as RestaurantDetailUiState.Loaded
                assertTrue(timedOut.enrichmentTimedOut)
            }
        }
}
