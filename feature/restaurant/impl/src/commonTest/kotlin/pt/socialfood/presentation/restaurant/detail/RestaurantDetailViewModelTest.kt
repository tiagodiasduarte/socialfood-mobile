package pt.socialfood.presentation.restaurant.detail

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.model.Location
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.fakes.FakeFavouriteRestaurantsRepository
import pt.socialfood.fakes.FakeRestaurantVisitStatusRepository
import pt.socialfood.fakes.FakeRestaurantsRepository
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
        restaurantsRepository: FakeRestaurantsRepository = FakeRestaurantsRepository(
            findByIdResult = Result.Success(fakeRestaurant),
        ),
        favouriteRestaurantsRepository: FakeFavouriteRestaurantsRepository = FakeFavouriteRestaurantsRepository(),
        restaurantVisitStatusRepository: FakeRestaurantVisitStatusRepository = FakeRestaurantVisitStatusRepository(),
    ) = RestaurantDetailViewModel(
        restaurantsRepository = restaurantsRepository,
        favouriteRestaurantsRepository = favouriteRestaurantsRepository,
        restaurantVisitStatusRepository = restaurantVisitStatusRepository,
        restaurantId = fakeRestaurant.id,
    )

    @Test
    fun `given restaurant is already a favourite when loaded then state reflects isFavourite true`() =
        runTestWithMainDispatcher {
            // Given
            val vm = createViewModel(
                favouriteRestaurantsRepository = FakeFavouriteRestaurantsRepository(
                    isFavouriteResult = Result.Success(true),
                ),
            )

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
        val vm = createViewModel(
            restaurantVisitStatusRepository = FakeRestaurantVisitStatusRepository(
                statusResult = Result.Success(VisitStatus.WISHLIST),
            ),
        )

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
            val favouriteRestaurantsRepository = FakeFavouriteRestaurantsRepository()
            val vm = createViewModel(favouriteRestaurantsRepository = favouriteRestaurantsRepository)

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
            assertEquals(1, favouriteRestaurantsRepository.markInvokeCount)
            assertEquals(fakeRestaurant, favouriteRestaurantsRepository.lastMarkedRestaurant)
        }

    @Test
    fun `given restaurant is a favourite when toggleFavourite is called then flips isFavourite and calls unmark`() =
        runTestWithMainDispatcher {
            // Given
            val favouriteRestaurantsRepository = FakeFavouriteRestaurantsRepository(
                isFavouriteResult = Result.Success(true),
            )
            val vm = createViewModel(
                favouriteRestaurantsRepository = favouriteRestaurantsRepository,
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
            assertEquals(1, favouriteRestaurantsRepository.unmarkInvokeCount)
            assertEquals(fakeRestaurant.id, favouriteRestaurantsRepository.lastUnmarkedRestaurantId)
        }

    @Test
    fun `given mark fails when toggleFavourite is called then reverts the optimistic flip`() =
        runTestWithMainDispatcher {
            // Given
            val vm = createViewModel(
                favouriteRestaurantsRepository = FakeFavouriteRestaurantsRepository(
                    markResult = Result.Failure(DataError.Network(Exception("test error"))),
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
            val visitStatusRepository = FakeRestaurantVisitStatusRepository()
            val vm = createViewModel(restaurantVisitStatusRepository = visitStatusRepository)

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
            assertEquals(fakeRestaurant, visitStatusRepository.lastMarkedRestaurant)
            assertEquals(VisitStatus.WISHLIST, visitStatusRepository.lastMarkedStatus)
        }

    @Test
    fun `given the restaurant is wished when moveToVisited is called then sets visitStatus to VISITED and marks it`() =
        runTestWithMainDispatcher {
            // Given
            val visitStatusRepository = FakeRestaurantVisitStatusRepository(
                statusResult = Result.Success(VisitStatus.WISHLIST),
            )
            val vm = createViewModel(
                restaurantVisitStatusRepository = visitStatusRepository,
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
            assertEquals(fakeRestaurant, visitStatusRepository.lastMarkedRestaurant)
            assertEquals(VisitStatus.VISITED, visitStatusRepository.lastMarkedStatus)
        }

    @Test
    fun `given mark fails when addToWishlist is called then reverts the optimistic visitStatus`() =
        runTestWithMainDispatcher {
            // Given
            val vm = createViewModel(
                restaurantVisitStatusRepository = FakeRestaurantVisitStatusRepository(
                    markResult = Result.Failure(DataError.Network(Exception("test error"))),
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
