package pt.socialfood.presentation.restaurant.detail

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.model.Location
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.fakes.FakeGetRestaurantByIdUseCase
import pt.socialfood.fakes.FakeGetVisitStatusUseCase
import pt.socialfood.fakes.FakeIsRestaurantFavouriteUseCase
import pt.socialfood.fakes.FakeMarkRestaurantFavouriteUseCase
import pt.socialfood.fakes.FakeMarkRestaurantVisitStatusUseCase
import pt.socialfood.fakes.FakeUnmarkRestaurantFavouriteUseCase
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
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
            imagesUrl = emptyList(),
            address = "Rua Augusta 1",
            rating = 4.5,
            userRatingCount = 100,
            websiteUrl = null,
            phoneNumber = "+351910000000",
            location = Location(latitude = 38.7223, longitude = -9.1393),
        )

    private fun createViewModel(
        getRestaurantById: FakeGetRestaurantByIdUseCase = FakeGetRestaurantByIdUseCase(Result.Success(fakeRestaurant)),
        isRestaurantFavourite: FakeIsRestaurantFavouriteUseCase =
            FakeIsRestaurantFavouriteUseCase(Result.Success(false)),
        markRestaurantFavourite: FakeMarkRestaurantFavouriteUseCase = FakeMarkRestaurantFavouriteUseCase(),
        unmarkRestaurantFavourite: FakeUnmarkRestaurantFavouriteUseCase = FakeUnmarkRestaurantFavouriteUseCase(),
        getVisitStatus: FakeGetVisitStatusUseCase = FakeGetVisitStatusUseCase(),
        markRestaurantVisitStatus: FakeMarkRestaurantVisitStatusUseCase = FakeMarkRestaurantVisitStatusUseCase(),
    ) = RestaurantDetailViewModel(
        getRestaurantById = getRestaurantById,
        isRestaurantFavourite = isRestaurantFavourite,
        markRestaurantFavourite = markRestaurantFavourite,
        unmarkRestaurantFavourite = unmarkRestaurantFavourite,
        getVisitStatus = getVisitStatus,
        markRestaurantVisitStatus = markRestaurantVisitStatus,
        restaurantId = fakeRestaurant.id,
    )

    @Test
    fun `given restaurant is already a favourite when loaded then state reflects isFavourite true`() =
        runTestWithMainDispatcher {
            // Given
            val vm = createViewModel(isRestaurantFavourite = FakeIsRestaurantFavouriteUseCase(Result.Success(true)))

            // When / Then
            vm.state.test {
                assertEquals(RestaurantDetailUiState.Loading, awaitItem())
                val loaded = assertIs<RestaurantDetailUiState.Loaded>(awaitItem())
                assertTrue(loaded.isFavourite)
            }
        }

    @Test
    fun `given restaurant already has a visit status when loaded then state reflects it`() = runTestWithMainDispatcher {
        // Given
        val vm = createViewModel(getVisitStatus = FakeGetVisitStatusUseCase(Result.Success(VisitStatus.WISHLIST)))

        // When / Then
        vm.state.test {
            assertEquals(RestaurantDetailUiState.Loading, awaitItem())
            val loaded = assertIs<RestaurantDetailUiState.Loaded>(awaitItem())
            assertEquals(VisitStatus.WISHLIST, loaded.visitStatus)
        }
    }

    @Test
    fun `given restaurant is not a favourite when toggleFavourite is called then flips isFavourite and calls mark`() =
        runTestWithMainDispatcher {
            // Given
            val mark = FakeMarkRestaurantFavouriteUseCase()
            val vm = createViewModel(markRestaurantFavourite = mark)

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
            val vm = createViewModel(
                isRestaurantFavourite = FakeIsRestaurantFavouriteUseCase(Result.Success(true)),
                unmarkRestaurantFavourite = unmark,
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
            val vm = createViewModel(
                markRestaurantFavourite = FakeMarkRestaurantFavouriteUseCase(
                    Result.Failure(DataError.Network(Exception("test error"))),
                ),
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

    @Test
    fun `given no visit status when addToWishlist is called then sets visitStatus to WISHLIST and marks it`() =
        runTestWithMainDispatcher {
            // Given
            val mark = FakeMarkRestaurantVisitStatusUseCase()
            val vm = createViewModel(markRestaurantVisitStatus = mark)

            // When / Then
            vm.state.test {
                assertEquals(RestaurantDetailUiState.Loading, awaitItem())
                val initial = assertIs<RestaurantDetailUiState.Loaded>(awaitItem())
                assertNull(initial.visitStatus)

                vm.addToWishlist()

                val updated = assertIs<RestaurantDetailUiState.Loaded>(awaitItem())
                assertEquals(VisitStatus.WISHLIST, updated.visitStatus)

                cancelAndIgnoreRemainingEvents()
            }

            advanceUntilIdle()
            assertEquals(fakeRestaurant, mark.lastMarkedRestaurant)
            assertEquals(VisitStatus.WISHLIST, mark.lastStatus)
        }

    @Test
    fun `given the restaurant is wished when moveToVisited is called then sets visitStatus to VISITED and marks it`() =
        runTestWithMainDispatcher {
            // Given
            val mark = FakeMarkRestaurantVisitStatusUseCase()
            val vm = createViewModel(
                getVisitStatus = FakeGetVisitStatusUseCase(Result.Success(VisitStatus.WISHLIST)),
                markRestaurantVisitStatus = mark,
            )

            // When / Then
            vm.state.test {
                assertEquals(RestaurantDetailUiState.Loading, awaitItem())
                val initial = assertIs<RestaurantDetailUiState.Loaded>(awaitItem())
                assertEquals(VisitStatus.WISHLIST, initial.visitStatus)

                vm.moveToVisited()

                val updated = assertIs<RestaurantDetailUiState.Loaded>(awaitItem())
                assertEquals(VisitStatus.VISITED, updated.visitStatus)

                cancelAndIgnoreRemainingEvents()
            }

            advanceUntilIdle()
            assertEquals(fakeRestaurant, mark.lastMarkedRestaurant)
            assertEquals(VisitStatus.VISITED, mark.lastStatus)
        }

    @Test
    fun `given mark fails when addToWishlist is called then reverts the optimistic visitStatus`() =
        runTestWithMainDispatcher {
            // Given
            val vm = createViewModel(
                markRestaurantVisitStatus = FakeMarkRestaurantVisitStatusUseCase(
                    Result.Failure(DataError.Network(Exception("test error"))),
                ),
            )

            // When / Then
            vm.state.test {
                assertEquals(RestaurantDetailUiState.Loading, awaitItem())
                val initial = assertIs<RestaurantDetailUiState.Loaded>(awaitItem())
                assertNull(initial.visitStatus)

                vm.addToWishlist()

                val updated = assertIs<RestaurantDetailUiState.Loaded>(awaitItem())
                assertEquals(VisitStatus.WISHLIST, updated.visitStatus)

                val reverted = assertIs<RestaurantDetailUiState.Loaded>(awaitItem())
                assertNull(reverted.visitStatus)
            }
        }
}
