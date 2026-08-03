package pt.socialfood.presentation.favourite.restaurant

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.core.Result
import pt.socialfood.domain.error.ApiError
import pt.socialfood.domain.model.FavouriteRestaurant
import pt.socialfood.domain.model.PagedFavouriteRestaurants
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.fakes.FakeGetFavouriteRestaurantsUseCase
import pt.socialfood.fakes.FakeUnmarkRestaurantFavouriteUseCase
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class FavouriteRestaurantsViewModelTest {
    private fun favourite(id: String) =
        FavouriteRestaurant(
            restaurant =
                Restaurant(
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
            favouritedAt = 0L,
        )

    @Test
    fun `given favourites exist when created then loads first page into Loaded state`() =
        runTestWithMainDispatcher {
            // Given
            val useCase =
                FakeGetFavouriteRestaurantsUseCase {
                    Result.Success(
                        PagedFavouriteRestaurants(
                            favourites = listOf(favourite("r1")),
                            page = it,
                            total = 1,
                            hasMore = false,
                        ),
                    )
                }

            // When / Then
            val vm = FavouriteRestaurantsViewModel(useCase, FakeUnmarkRestaurantFavouriteUseCase())
            vm.state.test {
                assertEquals(FavouriteRestaurantsUiState.Loading, awaitItem())
                val state = assertIs<FavouriteRestaurantsUiState.Loaded>(awaitItem())
                assertEquals(1, state.restaurants.size)
                assertEquals("r1", state.restaurants.first().id)
            }
        }

    @Test
    fun `given use case fails when created then state is Error`() =
        runTestWithMainDispatcher {
            // Given
            val useCase = FakeGetFavouriteRestaurantsUseCase { Result.Failure(ApiError.Network(Exception("test error"))) }

            // When / Then
            val vm = FavouriteRestaurantsViewModel(useCase, FakeUnmarkRestaurantFavouriteUseCase())
            vm.state.test {
                assertEquals(FavouriteRestaurantsUiState.Loading, awaitItem())
                assertEquals(FavouriteRestaurantsUiState.Error, awaitItem())
            }
        }

    @Test
    fun `given more pages available when loadMore is called then appends restaurants and updates hasMore`() =
        runTestWithMainDispatcher {
            // Given
            val useCase =
                FakeGetFavouriteRestaurantsUseCase { page ->
                    if (page == 1) {
                        Result.Success(
                            PagedFavouriteRestaurants(
                                favourites = listOf(favourite("r1")),
                                page = 1,
                                total = 2,
                                hasMore = true,
                            ),
                        )
                    } else {
                        Result.Success(
                            PagedFavouriteRestaurants(
                                favourites = listOf(favourite("r2")),
                                page = 2,
                                total = 2,
                                hasMore = false,
                            ),
                        )
                    }
                }
            val vm = FavouriteRestaurantsViewModel(useCase, FakeUnmarkRestaurantFavouriteUseCase())

            // When / Then
            vm.state.test {
                assertEquals(FavouriteRestaurantsUiState.Loading, awaitItem())
                val first = assertIs<FavouriteRestaurantsUiState.Loaded>(awaitItem())
                assertEquals(listOf("r1"), first.restaurants.map { it.id })

                vm.loadMore()

                val loadingMore = assertIs<FavouriteRestaurantsUiState.Loaded>(awaitItem())
                assertTrue(loadingMore.isLoadingMore)

                val loaded = assertIs<FavouriteRestaurantsUiState.Loaded>(awaitItem())
                assertEquals(listOf("r1", "r2"), loaded.restaurants.map { it.id })
                assertFalse(loaded.hasMore)
            }
        }

    @Test
    fun `given refresh is called then reloads first page and clears isRefreshing`() =
        runTestWithMainDispatcher {
            // Given
            val useCase =
                FakeGetFavouriteRestaurantsUseCase {
                    Result.Success(
                        PagedFavouriteRestaurants(
                            favourites = listOf(favourite("r1")),
                            page = it,
                            total = 1,
                            hasMore = false,
                        ),
                    )
                }
            val vm = FavouriteRestaurantsViewModel(useCase, FakeUnmarkRestaurantFavouriteUseCase())

            // When / Then
            vm.state.test {
                assertEquals(FavouriteRestaurantsUiState.Loading, awaitItem())
                assertIs<FavouriteRestaurantsUiState.Loaded>(awaitItem())
            }

            vm.refresh()
            advanceUntilIdle()
            assertFalse(vm.isRefreshing.value)
            assertIs<FavouriteRestaurantsUiState.Loaded>(vm.state.value)
        }

    @Test
    fun `given a favourite restaurant when removeFavourite succeeds then removes it from state`() =
        runTestWithMainDispatcher {
            // Given
            val useCase =
                FakeGetFavouriteRestaurantsUseCase {
                    Result.Success(
                        PagedFavouriteRestaurants(
                            favourites = listOf(favourite("r1"), favourite("r2")),
                            page = it,
                            total = 2,
                            hasMore = false,
                        ),
                    )
                }
            val unmarkUseCase = FakeUnmarkRestaurantFavouriteUseCase()
            val vm = FavouriteRestaurantsViewModel(useCase, unmarkUseCase)

            // When / Then
            vm.state.test {
                assertEquals(FavouriteRestaurantsUiState.Loading, awaitItem())
                val loaded = assertIs<FavouriteRestaurantsUiState.Loaded>(awaitItem())
                assertEquals(listOf("r1", "r2"), loaded.restaurants.map { it.id })

                vm.removeFavourite("r1")

                val afterRemoval = assertIs<FavouriteRestaurantsUiState.Loaded>(awaitItem())
                assertEquals(listOf("r2"), afterRemoval.restaurants.map { it.id })
            }
            advanceUntilIdle()
            assertEquals(1, unmarkUseCase.invokeCount)
            assertEquals("r1", unmarkUseCase.lastRestaurantId)
        }

    @Test
    fun `given removeFavourite fails when called then reverts the restaurant back into state`() =
        runTestWithMainDispatcher {
            // Given
            val useCase =
                FakeGetFavouriteRestaurantsUseCase {
                    Result.Success(
                        PagedFavouriteRestaurants(
                            favourites = listOf(favourite("r1")),
                            page = it,
                            total = 1,
                            hasMore = false,
                        ),
                    )
                }
            val unmarkUseCase =
                FakeUnmarkRestaurantFavouriteUseCase(result = Result.Failure(ApiError.Network(Exception("test error"))))
            val vm = FavouriteRestaurantsViewModel(useCase, unmarkUseCase)

            // When / Then
            vm.state.test {
                assertEquals(FavouriteRestaurantsUiState.Loading, awaitItem())
                val loaded = assertIs<FavouriteRestaurantsUiState.Loaded>(awaitItem())
                assertEquals(listOf("r1"), loaded.restaurants.map { it.id })

                vm.removeFavourite("r1")

                val afterRemoval = assertIs<FavouriteRestaurantsUiState.Loaded>(awaitItem())
                assertTrue(afterRemoval.restaurants.isEmpty())

                val reverted = assertIs<FavouriteRestaurantsUiState.Loaded>(awaitItem())
                assertEquals(listOf("r1"), reverted.restaurants.map { it.id })
            }
        }
}
