package pt.socialfood.presentation.wishlist.restaurant

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.domain.model.PagedWishlistRestaurants
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.WishlistRestaurant
import pt.socialfood.fakes.FakeGetWishlistRestaurantsUseCase
import pt.socialfood.fakes.FakeUnmarkRestaurantWishlistUseCase
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class WishlistRestaurantsViewModelTest {
    private fun wishlisted(id: String) = WishlistRestaurant(
        restaurant = Restaurant(
            id = id,
            name = "Restaurant $id",
            description = "",
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
        ),
        wishlistedAt = 0L,
    )

    @Test
    fun `given wishlist exists when created then loads first page into Loaded state`() = runTestWithMainDispatcher {
        // Given
        val useCase = FakeGetWishlistRestaurantsUseCase {
            Result.Success(
                PagedWishlistRestaurants(wishlist = listOf(wishlisted("r1")), page = it, total = 1, hasMore = false),
            )
        }

        // When / Then
        val vm = WishlistRestaurantsViewModel(useCase, FakeUnmarkRestaurantWishlistUseCase())
        vm.state.test {
            assertEquals(WishlistRestaurantsUiState.Loading, awaitItem())
            val state = assertIs<WishlistRestaurantsUiState.Loaded>(awaitItem())
            assertEquals(1, state.restaurants.size)
            assertEquals("r1", state.restaurants.first().id)
        }
    }

    @Test
    fun `given use case fails when created then state is Error`() = runTestWithMainDispatcher {
        // Given
        val useCase = FakeGetWishlistRestaurantsUseCase { Result.Failure(DataError.Network(Exception("test error"))) }

        // When / Then
        val vm = WishlistRestaurantsViewModel(useCase, FakeUnmarkRestaurantWishlistUseCase())
        vm.state.test {
            assertEquals(WishlistRestaurantsUiState.Loading, awaitItem())
            assertEquals(WishlistRestaurantsUiState.Error(ErrorCode.NETWORK), awaitItem())
        }
    }

    @Test
    fun `given more pages available when loadMore is called then appends restaurants and updates hasMore`() =
        runTestWithMainDispatcher {
            // Given
            val useCase = FakeGetWishlistRestaurantsUseCase { page ->
                if (page == 1) {
                    Result.Success(
                        PagedWishlistRestaurants(
                            wishlist = listOf(wishlisted("r1")),
                            page = 1,
                            total = 2,
                            hasMore = true,
                        ),
                    )
                } else {
                    Result.Success(
                        PagedWishlistRestaurants(
                            wishlist = listOf(wishlisted("r2")),
                            page = 2,
                            total = 2,
                            hasMore = false,
                        ),
                    )
                }
            }
            val vm = WishlistRestaurantsViewModel(useCase, FakeUnmarkRestaurantWishlistUseCase())

            // When / Then
            vm.state.test {
                assertEquals(WishlistRestaurantsUiState.Loading, awaitItem())
                val first = assertIs<WishlistRestaurantsUiState.Loaded>(awaitItem())
                assertEquals(listOf("r1"), first.restaurants.map { it.id })

                vm.loadMore()

                val loadingMore = assertIs<WishlistRestaurantsUiState.Loaded>(awaitItem())
                assertTrue(loadingMore.isLoadingMore)

                val loaded = assertIs<WishlistRestaurantsUiState.Loaded>(awaitItem())
                assertEquals(listOf("r1", "r2"), loaded.restaurants.map { it.id })
                assertFalse(loaded.hasMore)
            }
        }

    @Test
    fun `given refresh is called then reloads first page and clears isRefreshing`() = runTestWithMainDispatcher {
        // Given
        val useCase = FakeGetWishlistRestaurantsUseCase {
            Result.Success(
                PagedWishlistRestaurants(wishlist = listOf(wishlisted("r1")), page = it, total = 1, hasMore = false),
            )
        }
        val vm = WishlistRestaurantsViewModel(useCase, FakeUnmarkRestaurantWishlistUseCase())

        // When / Then
        vm.state.test {
            assertEquals(WishlistRestaurantsUiState.Loading, awaitItem())
            assertIs<WishlistRestaurantsUiState.Loaded>(awaitItem())
        }

        vm.refresh()
        advanceUntilIdle()
        assertFalse(vm.isRefreshing.value)
        assertIs<WishlistRestaurantsUiState.Loaded>(vm.state.value)
    }

    @Test
    fun `given a wishlisted restaurant when removeFromWishlist succeeds then removes it from state`() =
        runTestWithMainDispatcher {
            // Given
            val useCase = FakeGetWishlistRestaurantsUseCase {
                Result.Success(
                    PagedWishlistRestaurants(
                        wishlist = listOf(wishlisted("r1"), wishlisted("r2")),
                        page = it,
                        total = 2,
                        hasMore = false,
                    ),
                )
            }
            val unmarkUseCase = FakeUnmarkRestaurantWishlistUseCase()
            val vm = WishlistRestaurantsViewModel(useCase, unmarkUseCase)

            // When / Then
            vm.state.test {
                assertEquals(WishlistRestaurantsUiState.Loading, awaitItem())
                val loaded = assertIs<WishlistRestaurantsUiState.Loaded>(awaitItem())
                assertEquals(listOf("r1", "r2"), loaded.restaurants.map { it.id })

                vm.removeFromWishlist("r1")

                val afterRemoval = assertIs<WishlistRestaurantsUiState.Loaded>(awaitItem())
                assertEquals(listOf("r2"), afterRemoval.restaurants.map { it.id })
            }
            advanceUntilIdle()
            assertEquals(1, unmarkUseCase.invokeCount)
            assertEquals("r1", unmarkUseCase.lastRestaurantId)
        }

    @Test
    fun `given removeFromWishlist fails when called then reverts the restaurant back into state`() =
        runTestWithMainDispatcher {
            // Given
            val useCase = FakeGetWishlistRestaurantsUseCase {
                Result.Success(
                    PagedWishlistRestaurants(
                        wishlist = listOf(wishlisted("r1")),
                        page = it,
                        total = 1,
                        hasMore = false,
                    ),
                )
            }
            val unmarkUseCase = FakeUnmarkRestaurantWishlistUseCase(
                result = Result.Failure(DataError.Network(Exception("test error"))),
            )
            val vm = WishlistRestaurantsViewModel(useCase, unmarkUseCase)

            // When / Then
            vm.state.test {
                assertEquals(WishlistRestaurantsUiState.Loading, awaitItem())
                val loaded = assertIs<WishlistRestaurantsUiState.Loaded>(awaitItem())
                assertEquals(listOf("r1"), loaded.restaurants.map { it.id })

                vm.removeFromWishlist("r1")

                val afterRemoval = assertIs<WishlistRestaurantsUiState.Loaded>(awaitItem())
                assertTrue(afterRemoval.restaurants.isEmpty())

                val reverted = assertIs<WishlistRestaurantsUiState.Loaded>(awaitItem())
                assertEquals(listOf("r1"), reverted.restaurants.map { it.id })
            }
        }
}
