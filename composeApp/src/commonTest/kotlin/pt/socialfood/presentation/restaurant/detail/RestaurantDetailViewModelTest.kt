package pt.socialfood.presentation.restaurant.detail

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.core.Result
import pt.socialfood.domain.error.ErrorEntity
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.fakes.FakeGetRestaurantByIdUseCase
import pt.socialfood.fakes.FakeIsRestaurantFavouriteUseCase
import pt.socialfood.fakes.FakeMarkRestaurantFavouriteUseCase
import pt.socialfood.fakes.FakeUnmarkRestaurantFavouriteUseCase
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RestaurantDetailViewModelTest {
    private val fakeRestaurant =
        Restaurant(
            id = "restaurant-id",
            name = "Restaurant Name",
            description = "Restaurant Description",
            city = "Lisbon",
            country = "Portugal",
            countryCode = "PT",
            postalCode = "1000-000",
            photoNames = emptyList(),
            address = "Rua Augusta 1",
            rating = 4.5,
            userRatingCount = 100,
            websiteUrl = null,
            phoneNumber = "+351910000000",
        )

    @Test
    fun `given restaurant is already a favourite when loaded then state reflects isFavourite true`() =
        runTestWithMainDispatcher {
            // Given
            val vm =
                RestaurantDetailViewModel(
                    getRestaurantById = FakeGetRestaurantByIdUseCase(Result.Success(fakeRestaurant)),
                    isRestaurantFavourite = FakeIsRestaurantFavouriteUseCase(Result.Success(true)),
                    markRestaurantFavourite = FakeMarkRestaurantFavouriteUseCase(),
                    unmarkRestaurantFavourite = FakeUnmarkRestaurantFavouriteUseCase(),
                    restaurantId = fakeRestaurant.id,
                )

            // When / Then
            vm.state.test {
                assertEquals(RestaurantDetailUiState.Loading, awaitItem())
                val loaded = assertIs<RestaurantDetailUiState.Loaded>(awaitItem())
                assertTrue(loaded.isFavourite)
            }
        }

    @Test
    @Suppress("MaxLineLength")
    fun `given restaurant is not a favourite when toggleFavourite is called then flips isFavourite optimistically and calls mark`() =
        runTestWithMainDispatcher {
            // Given
            val mark = FakeMarkRestaurantFavouriteUseCase()
            val vm =
                RestaurantDetailViewModel(
                    getRestaurantById = FakeGetRestaurantByIdUseCase(Result.Success(fakeRestaurant)),
                    isRestaurantFavourite = FakeIsRestaurantFavouriteUseCase(Result.Success(false)),
                    markRestaurantFavourite = mark,
                    unmarkRestaurantFavourite = FakeUnmarkRestaurantFavouriteUseCase(),
                    restaurantId = fakeRestaurant.id,
                )

            // When / Then
            vm.state.test {
                assertEquals(RestaurantDetailUiState.Loading, awaitItem())
                val initial = assertIs<RestaurantDetailUiState.Loaded>(awaitItem())
                assertFalse(initial.isFavourite)

                vm.toggleFavourite()

                val flipped = assertIs<RestaurantDetailUiState.Loaded>(awaitItem())
                assertTrue(flipped.isFavourite)

                cancelAndIgnoreRemainingEvents()
            }

            advanceUntilIdle()
            assertEquals(1, mark.invokeCount)
            assertEquals(fakeRestaurant, mark.lastRestaurant)
        }

    @Test
    fun `given restaurant is a favourite when toggleFavourite is called then flips isFavourite and calls unmark`() =
        runTestWithMainDispatcher {
            // Given
            val unmark = FakeUnmarkRestaurantFavouriteUseCase()
            val vm =
                RestaurantDetailViewModel(
                    getRestaurantById = FakeGetRestaurantByIdUseCase(Result.Success(fakeRestaurant)),
                    isRestaurantFavourite = FakeIsRestaurantFavouriteUseCase(Result.Success(true)),
                    markRestaurantFavourite = FakeMarkRestaurantFavouriteUseCase(),
                    unmarkRestaurantFavourite = unmark,
                    restaurantId = fakeRestaurant.id,
                )

            // When / Then
            vm.state.test {
                assertEquals(RestaurantDetailUiState.Loading, awaitItem())
                val initial = assertIs<RestaurantDetailUiState.Loaded>(awaitItem())
                assertTrue(initial.isFavourite)

                vm.toggleFavourite()

                val flipped = assertIs<RestaurantDetailUiState.Loaded>(awaitItem())
                assertFalse(flipped.isFavourite)

                cancelAndIgnoreRemainingEvents()
            }

            advanceUntilIdle()
            assertEquals(1, unmark.invokeCount)
            assertEquals(fakeRestaurant.id, unmark.lastRestaurantId)
        }

    @Test
    fun `given mark fails when toggleFavourite is called then reverts the optimistic flip`() =
        runTestWithMainDispatcher {
            // Given
            val vm =
                RestaurantDetailViewModel(
                    getRestaurantById = FakeGetRestaurantByIdUseCase(Result.Success(fakeRestaurant)),
                    isRestaurantFavourite = FakeIsRestaurantFavouriteUseCase(Result.Success(false)),
                    markRestaurantFavourite = FakeMarkRestaurantFavouriteUseCase(Result.Error(ErrorEntity.Unknown)),
                    unmarkRestaurantFavourite = FakeUnmarkRestaurantFavouriteUseCase(),
                    restaurantId = fakeRestaurant.id,
                )

            // When / Then
            vm.state.test {
                assertEquals(RestaurantDetailUiState.Loading, awaitItem())
                val initial = assertIs<RestaurantDetailUiState.Loaded>(awaitItem())
                assertFalse(initial.isFavourite)

                vm.toggleFavourite()

                val flipped = assertIs<RestaurantDetailUiState.Loaded>(awaitItem())
                assertTrue(flipped.isFavourite)

                val reverted = assertIs<RestaurantDetailUiState.Loaded>(awaitItem())
                assertFalse(reverted.isFavourite)
            }
        }
}
