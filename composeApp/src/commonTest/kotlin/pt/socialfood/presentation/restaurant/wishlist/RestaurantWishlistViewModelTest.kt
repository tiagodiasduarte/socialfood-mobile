package pt.socialfood.presentation.restaurant.wishlist

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.domain.model.PagedRestaurantVisitStatus
import pt.socialfood.domain.model.RestaurantVisitStatus
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.fakes.FakeGetRestaurantVisitStatusUseCase
import pt.socialfood.fakes.FakeUnmarkRestaurantVisitStatusUseCase
import pt.socialfood.random.nextRestaurant
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RestaurantWishlistViewModelTest {
    private fun wished(id: String) = RestaurantVisitStatus(
        restaurant = Random.nextRestaurant(id = id),
        status = VisitStatus.WISH,
        recordedAt = 0L,
    )

    @Test
    fun `given wish list exists when created then loads first page into Loaded state`() = runTestWithMainDispatcher {
        // Given
        val useCase = FakeGetRestaurantVisitStatusUseCase {
            Result.Success(
                PagedRestaurantVisitStatus(visits = listOf(wished("r1")), page = it, total = 1, hasMore = false),
            )
        }

        // When / Then
        val vm = RestaurantWishlistViewModel(useCase, FakeUnmarkRestaurantVisitStatusUseCase())
        vm.state.test {
            assertEquals(RestaurantWishlistUiState.Loading, awaitItem())
            val state = assertIs<RestaurantWishlistUiState.Loaded>(awaitItem())
            assertEquals(1, state.restaurants.size)
            assertEquals("r1", state.restaurants.first().id)
        }
        assertEquals(VisitStatus.WISH, useCase.lastStatus)
    }

    @Test
    fun `given use case fails when created then state is Error`() = runTestWithMainDispatcher {
        // Given
        val useCase = FakeGetRestaurantVisitStatusUseCase { Result.Failure(DataError.Network(Exception("test error"))) }

        // When / Then
        val vm = RestaurantWishlistViewModel(useCase, FakeUnmarkRestaurantVisitStatusUseCase())
        vm.state.test {
            assertEquals(RestaurantWishlistUiState.Loading, awaitItem())
            assertEquals(RestaurantWishlistUiState.Error(ErrorCode.NETWORK), awaitItem())
        }
    }

    @Test
    fun `given more pages available when loadMore is called then appends restaurants and updates hasMore`() =
        runTestWithMainDispatcher {
            // Given
            val useCase = FakeGetRestaurantVisitStatusUseCase { page ->
                if (page == 1) {
                    Result.Success(
                        PagedRestaurantVisitStatus(
                            visits = listOf(wished("r1")),
                            page = 1,
                            total = 2,
                            hasMore = true,
                        ),
                    )
                } else {
                    Result.Success(
                        PagedRestaurantVisitStatus(
                            visits = listOf(wished("r2")),
                            page = 2,
                            total = 2,
                            hasMore = false,
                        ),
                    )
                }
            }
            val vm = RestaurantWishlistViewModel(useCase, FakeUnmarkRestaurantVisitStatusUseCase())

            // When / Then
            vm.state.test {
                assertEquals(RestaurantWishlistUiState.Loading, awaitItem())
                val first = assertIs<RestaurantWishlistUiState.Loaded>(awaitItem())
                assertEquals(listOf("r1"), first.restaurants.map { it.id })

                vm.loadMore()

                val loadingMore = assertIs<RestaurantWishlistUiState.Loaded>(awaitItem())
                assertTrue(loadingMore.isLoadingMore)

                val loaded = assertIs<RestaurantWishlistUiState.Loaded>(awaitItem())
                assertEquals(listOf("r1", "r2"), loaded.restaurants.map { it.id })
                assertFalse(loaded.hasMore)
            }
        }

    @Test
    fun `given refresh is called then reloads first page and clears isRefreshing`() = runTestWithMainDispatcher {
        // Given
        val useCase = FakeGetRestaurantVisitStatusUseCase {
            Result.Success(
                PagedRestaurantVisitStatus(visits = listOf(wished("r1")), page = it, total = 1, hasMore = false),
            )
        }
        val vm = RestaurantWishlistViewModel(useCase, FakeUnmarkRestaurantVisitStatusUseCase())

        // When / Then
        vm.state.test {
            assertEquals(RestaurantWishlistUiState.Loading, awaitItem())
            assertIs<RestaurantWishlistUiState.Loaded>(awaitItem())
        }

        vm.refresh()
        advanceUntilIdle()
        assertFalse(vm.isRefreshing.value)
        assertIs<RestaurantWishlistUiState.Loaded>(vm.state.value)
    }

    @Test
    fun `given a wished restaurant when removeFromWishlist succeeds then removes it from state`() =
        runTestWithMainDispatcher {
            // Given
            val useCase = FakeGetRestaurantVisitStatusUseCase {
                Result.Success(
                    PagedRestaurantVisitStatus(
                        visits = listOf(wished("r1"), wished("r2")),
                        page = it,
                        total = 2,
                        hasMore = false,
                    ),
                )
            }
            val unmarkUseCase = FakeUnmarkRestaurantVisitStatusUseCase()
            val vm = RestaurantWishlistViewModel(useCase, unmarkUseCase)

            // When / Then
            vm.state.test {
                assertEquals(RestaurantWishlistUiState.Loading, awaitItem())
                val loaded = assertIs<RestaurantWishlistUiState.Loaded>(awaitItem())
                assertEquals(listOf("r1", "r2"), loaded.restaurants.map { it.id })

                vm.removeFromWishlist("r1")

                val afterRemoval = assertIs<RestaurantWishlistUiState.Loaded>(awaitItem())
                assertEquals(listOf("r2"), afterRemoval.restaurants.map { it.id })
            }
            advanceUntilIdle()
            assertEquals("r1", unmarkUseCase.lastUnmarkedRestaurantId)
            assertEquals(VisitStatus.WISH, unmarkUseCase.lastStatus)
        }

    @Test
    fun `given removeFromWishlist fails when called then reverts the restaurant back into state`() =
        runTestWithMainDispatcher {
            // Given
            val useCase = FakeGetRestaurantVisitStatusUseCase {
                Result.Success(
                    PagedRestaurantVisitStatus(visits = listOf(wished("r1")), page = it, total = 1, hasMore = false),
                )
            }
            val unmarkUseCase = FakeUnmarkRestaurantVisitStatusUseCase(
                result = Result.Failure(DataError.Network(Exception("test error"))),
            )
            val vm = RestaurantWishlistViewModel(useCase, unmarkUseCase)

            // When / Then
            vm.state.test {
                assertEquals(RestaurantWishlistUiState.Loading, awaitItem())
                val loaded = assertIs<RestaurantWishlistUiState.Loaded>(awaitItem())
                assertEquals(listOf("r1"), loaded.restaurants.map { it.id })

                vm.removeFromWishlist("r1")

                val afterRemoval = assertIs<RestaurantWishlistUiState.Loaded>(awaitItem())
                assertTrue(afterRemoval.restaurants.isEmpty())

                val reverted = assertIs<RestaurantWishlistUiState.Loaded>(awaitItem())
                assertEquals(listOf("r1"), reverted.restaurants.map { it.id })
            }
        }
}
