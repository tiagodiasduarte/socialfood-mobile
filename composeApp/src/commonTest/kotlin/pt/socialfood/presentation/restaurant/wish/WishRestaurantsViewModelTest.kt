package pt.socialfood.presentation.restaurant.wish

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.domain.model.PagedRestaurantVisitStatus
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.RestaurantVisitStatus
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.fakes.FakeGetRestaurantVisitStatusUseCase
import pt.socialfood.fakes.FakeUnmarkRestaurantVisitStatusUseCase
import pt.socialfood.presentation.restaurant.RestaurantVisitUiState
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class WishRestaurantsViewModelTest {
    private fun wished(id: String) = RestaurantVisitStatus(
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
        val vm = WishRestaurantsViewModel(useCase, FakeUnmarkRestaurantVisitStatusUseCase())
        vm.state.test {
            assertEquals(RestaurantVisitUiState.Loading, awaitItem())
            val state = assertIs<RestaurantVisitUiState.Loaded>(awaitItem())
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
        val vm = WishRestaurantsViewModel(useCase, FakeUnmarkRestaurantVisitStatusUseCase())
        vm.state.test {
            assertEquals(RestaurantVisitUiState.Loading, awaitItem())
            assertEquals(RestaurantVisitUiState.Error(ErrorCode.NETWORK), awaitItem())
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
            val vm = WishRestaurantsViewModel(useCase, FakeUnmarkRestaurantVisitStatusUseCase())

            // When / Then
            vm.state.test {
                assertEquals(RestaurantVisitUiState.Loading, awaitItem())
                val first = assertIs<RestaurantVisitUiState.Loaded>(awaitItem())
                assertEquals(listOf("r1"), first.restaurants.map { it.id })

                vm.loadMore()

                val loadingMore = assertIs<RestaurantVisitUiState.Loaded>(awaitItem())
                assertTrue(loadingMore.isLoadingMore)

                val loaded = assertIs<RestaurantVisitUiState.Loaded>(awaitItem())
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
        val vm = WishRestaurantsViewModel(useCase, FakeUnmarkRestaurantVisitStatusUseCase())

        // When / Then
        vm.state.test {
            assertEquals(RestaurantVisitUiState.Loading, awaitItem())
            assertIs<RestaurantVisitUiState.Loaded>(awaitItem())
        }

        vm.refresh()
        advanceUntilIdle()
        assertFalse(vm.isRefreshing.value)
        assertIs<RestaurantVisitUiState.Loaded>(vm.state.value)
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
            val vm = WishRestaurantsViewModel(useCase, unmarkUseCase)

            // When / Then
            vm.state.test {
                assertEquals(RestaurantVisitUiState.Loading, awaitItem())
                val loaded = assertIs<RestaurantVisitUiState.Loaded>(awaitItem())
                assertEquals(listOf("r1", "r2"), loaded.restaurants.map { it.id })

                vm.removeFromWishlist("r1")

                val afterRemoval = assertIs<RestaurantVisitUiState.Loaded>(awaitItem())
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
            val vm = WishRestaurantsViewModel(useCase, unmarkUseCase)

            // When / Then
            vm.state.test {
                assertEquals(RestaurantVisitUiState.Loading, awaitItem())
                val loaded = assertIs<RestaurantVisitUiState.Loaded>(awaitItem())
                assertEquals(listOf("r1"), loaded.restaurants.map { it.id })

                vm.removeFromWishlist("r1")

                val afterRemoval = assertIs<RestaurantVisitUiState.Loaded>(awaitItem())
                assertTrue(afterRemoval.restaurants.isEmpty())

                val reverted = assertIs<RestaurantVisitUiState.Loaded>(awaitItem())
                assertEquals(listOf("r1"), reverted.restaurants.map { it.id })
            }
        }
}
