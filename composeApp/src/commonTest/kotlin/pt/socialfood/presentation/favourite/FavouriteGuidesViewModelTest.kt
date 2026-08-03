package pt.socialfood.presentation.favourite

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.FavouriteGuide
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.domain.model.PagedFavouriteGuides
import pt.socialfood.fakes.FakeGetFavouriteGuidesUseCase
import pt.socialfood.fakes.FakeUnmarkGuideFavouriteUseCase
import pt.socialfood.presentation.favourite.guide.FavouriteGuidesUiState
import pt.socialfood.presentation.favourite.guide.FavouriteGuidesViewModel
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class FavouriteGuidesViewModelTest {
    private fun favourite(id: String) =
        FavouriteGuide(
            guide =
                Guide(
                    id = id,
                    name = "Guide $id",
                    description = "",
                    visibility = GuideVisibility.PUBLIC,
                    author = Author(id = "author-id", name = "Author", username = "author"),
                    numberOfRestaurant = 0,
                ),
            favouritedAt = 0L,
        )

    @Test
    fun `given favourites exist when created then loads first page into Loaded state`() =
        runTestWithMainDispatcher {
            // Given
            val useCase =
                FakeGetFavouriteGuidesUseCase {
                    Result.Success(
                        PagedFavouriteGuides(
                            favourites = listOf(favourite("g1")),
                            page = it,
                            total = 1,
                            hasMore = false,
                        ),
                    )
                }

            // When / Then
            val vm = FavouriteGuidesViewModel(useCase, FakeUnmarkGuideFavouriteUseCase())
            vm.state.test {
                assertEquals(FavouriteGuidesUiState.Loading, awaitItem())
                val state = assertIs<FavouriteGuidesUiState.Loaded>(awaitItem())
                assertEquals(1, state.guides.size)
                assertEquals("g1", state.guides.first().id)
            }
        }

    @Test
    fun `given use case fails when created then state is Error`() =
        runTestWithMainDispatcher {
            // Given
            val useCase = FakeGetFavouriteGuidesUseCase { Result.Failure(DataError.Network(Exception("test error"))) }

            // When / Then
            val vm = FavouriteGuidesViewModel(useCase, FakeUnmarkGuideFavouriteUseCase())
            vm.state.test {
                assertEquals(FavouriteGuidesUiState.Loading, awaitItem())
                assertEquals(FavouriteGuidesUiState.Error, awaitItem())
            }
        }

    @Test
    fun `given more pages available when loadMore is called then appends guides and updates hasMore`() =
        runTestWithMainDispatcher {
            // Given
            val useCase =
                FakeGetFavouriteGuidesUseCase { page ->
                    if (page == 1) {
                        Result.Success(
                            PagedFavouriteGuides(
                                favourites = listOf(favourite("g1")),
                                page = 1,
                                total = 2,
                                hasMore = true,
                            ),
                        )
                    } else {
                        Result.Success(
                            PagedFavouriteGuides(
                                favourites = listOf(favourite("g2")),
                                page = 2,
                                total = 2,
                                hasMore = false,
                            ),
                        )
                    }
                }
            val vm = FavouriteGuidesViewModel(useCase, FakeUnmarkGuideFavouriteUseCase())

            // When / Then
            vm.state.test {
                assertEquals(FavouriteGuidesUiState.Loading, awaitItem())
                val first = assertIs<FavouriteGuidesUiState.Loaded>(awaitItem())
                assertEquals(listOf("g1"), first.guides.map { it.id })

                vm.loadMore()

                val loadingMore = assertIs<FavouriteGuidesUiState.Loaded>(awaitItem())
                assertTrue(loadingMore.isLoadingMore)

                val loaded = assertIs<FavouriteGuidesUiState.Loaded>(awaitItem())
                assertEquals(listOf("g1", "g2"), loaded.guides.map { it.id })
                assertFalse(loaded.hasMore)
            }
        }

    @Test
    fun `given refresh is called then reloads first page and clears isRefreshing`() =
        runTestWithMainDispatcher {
            // Given
            val useCase =
                FakeGetFavouriteGuidesUseCase {
                    Result.Success(
                        PagedFavouriteGuides(
                            favourites = listOf(favourite("g1")),
                            page = it,
                            total = 1,
                            hasMore = false,
                        ),
                    )
                }
            val vm = FavouriteGuidesViewModel(useCase, FakeUnmarkGuideFavouriteUseCase())

            // When / Then
            vm.state.test {
                assertEquals(FavouriteGuidesUiState.Loading, awaitItem())
                assertIs<FavouriteGuidesUiState.Loaded>(awaitItem())
            }

            vm.refresh()
            advanceUntilIdle()
            assertFalse(vm.isRefreshing.value)
            assertIs<FavouriteGuidesUiState.Loaded>(vm.state.value)
        }

    @Test
    fun `given a favourite guide when removeFavourite succeeds then removes it from state`() =
        runTestWithMainDispatcher {
            // Given
            val useCase =
                FakeGetFavouriteGuidesUseCase {
                    Result.Success(
                        PagedFavouriteGuides(
                            favourites = listOf(favourite("g1"), favourite("g2")),
                            page = it,
                            total = 2,
                            hasMore = false,
                        ),
                    )
                }
            val unmarkUseCase = FakeUnmarkGuideFavouriteUseCase()
            val vm = FavouriteGuidesViewModel(useCase, unmarkUseCase)

            // When / Then
            vm.state.test {
                assertEquals(FavouriteGuidesUiState.Loading, awaitItem())
                val loaded = assertIs<FavouriteGuidesUiState.Loaded>(awaitItem())
                assertEquals(listOf("g1", "g2"), loaded.guides.map { it.id })

                vm.removeFavourite("g1")

                val afterRemoval = assertIs<FavouriteGuidesUiState.Loaded>(awaitItem())
                assertEquals(listOf("g2"), afterRemoval.guides.map { it.id })
            }
            advanceUntilIdle()
            assertEquals(1, unmarkUseCase.invokeCount)
            assertEquals("g1", unmarkUseCase.lastGuideId)
        }

    @Test
    fun `given removeFavourite fails when called then reverts the guide back into state`() =
        runTestWithMainDispatcher {
            // Given
            val useCase =
                FakeGetFavouriteGuidesUseCase {
                    Result.Success(
                        PagedFavouriteGuides(
                            favourites = listOf(favourite("g1")),
                            page = it,
                            total = 1,
                            hasMore = false,
                        ),
                    )
                }
            val unmarkUseCase =
                FakeUnmarkGuideFavouriteUseCase(result = Result.Failure(DataError.Network(Exception("test error"))))
            val vm = FavouriteGuidesViewModel(useCase, unmarkUseCase)

            // When / Then
            vm.state.test {
                assertEquals(FavouriteGuidesUiState.Loading, awaitItem())
                val loaded = assertIs<FavouriteGuidesUiState.Loaded>(awaitItem())
                assertEquals(listOf("g1"), loaded.guides.map { it.id })

                vm.removeFavourite("g1")

                val afterRemoval = assertIs<FavouriteGuidesUiState.Loaded>(awaitItem())
                assertTrue(afterRemoval.guides.isEmpty())

                val reverted = assertIs<FavouriteGuidesUiState.Loaded>(awaitItem())
                assertEquals(listOf("g1"), reverted.guides.map { it.id })
            }
        }
}
