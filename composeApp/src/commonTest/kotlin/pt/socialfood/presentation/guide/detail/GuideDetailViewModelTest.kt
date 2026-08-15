package pt.socialfood.presentation.guide.detail

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.domain.model.User
import pt.socialfood.fakes.FakeGetGuideByIdUseCase
import pt.socialfood.fakes.FakeGetUserMeUseCase
import pt.socialfood.fakes.FakeIsGuideFavouriteUseCase
import pt.socialfood.fakes.FakeMarkGuideFavouriteUseCase
import pt.socialfood.fakes.FakeUnmarkGuideFavouriteUseCase
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GuideDetailViewModelTest {
    private val fakeGuide =
        Guide(
            id = "guide-id",
            name = "Guide Name",
            description = "Guide Description",
            visibility = GuideVisibility.PUBLIC,
            author = Author(id = "author-id", name = "Author", username = "author"),
            numberOfRestaurant = 0,
        )

    private val fakeUser = User(id = "user-id", email = "user@test.com", name = "Test User", username = "testuser")

    @Test
    fun `given guide is already a favourite when loaded then state reflects isFavourite true`() =
        runTestWithMainDispatcher {
            // Given
            val vm =
                GuideDetailViewModel(
                    getGuideById = FakeGetGuideByIdUseCase(Result.Success(fakeGuide)),
                    getUserMe = FakeGetUserMeUseCase(Result.Success(fakeUser)),
                    isGuideFavourite = FakeIsGuideFavouriteUseCase(Result.Success(true)),
                    markGuideFavourite = FakeMarkGuideFavouriteUseCase(),
                    unmarkGuideFavourite = FakeUnmarkGuideFavouriteUseCase(),
                    guideId = fakeGuide.id,
                )

            // When / Then
            vm.state.test {
                assertEquals(GuideDetailUiState.Loading, awaitItem())
                val loaded = assertIs<GuideDetailUiState.Loaded>(awaitItem())
                assertTrue(loaded.isFavourite)
            }
        }

    @Test
    @Suppress("MaxLineLength", "ktlint:standard:max-line-length")
    fun `given guide is not a favourite when toggleFavourite is called then flips isFavourite optimistically and calls mark`() =
        runTestWithMainDispatcher {
            // Given
            val mark = FakeMarkGuideFavouriteUseCase()
            val vm =
                GuideDetailViewModel(
                    getGuideById = FakeGetGuideByIdUseCase(Result.Success(fakeGuide)),
                    getUserMe = FakeGetUserMeUseCase(Result.Success(fakeUser)),
                    isGuideFavourite = FakeIsGuideFavouriteUseCase(Result.Success(false)),
                    markGuideFavourite = mark,
                    unmarkGuideFavourite = FakeUnmarkGuideFavouriteUseCase(),
                    guideId = fakeGuide.id,
                )

            // When / Then
            vm.state.test {
                assertEquals(GuideDetailUiState.Loading, awaitItem())
                val initial = assertIs<GuideDetailUiState.Loaded>(awaitItem())
                assertFalse(initial.isFavourite)

                vm.toggleFavourite()

                val flipped = assertIs<GuideDetailUiState.Loaded>(awaitItem())
                assertTrue(flipped.isFavourite)

                cancelAndIgnoreRemainingEvents()
            }

            advanceUntilIdle()
            assertEquals(1, mark.invokeCount)
            assertEquals(fakeGuide, mark.lastGuide)
        }

    @Test
    fun `given guide is a favourite when toggleFavourite is called then flips isFavourite and calls unmark`() =
        runTestWithMainDispatcher {
            // Given
            val unmark = FakeUnmarkGuideFavouriteUseCase()
            val vm =
                GuideDetailViewModel(
                    getGuideById = FakeGetGuideByIdUseCase(Result.Success(fakeGuide)),
                    getUserMe = FakeGetUserMeUseCase(Result.Success(fakeUser)),
                    isGuideFavourite = FakeIsGuideFavouriteUseCase(Result.Success(true)),
                    markGuideFavourite = FakeMarkGuideFavouriteUseCase(),
                    unmarkGuideFavourite = unmark,
                    guideId = fakeGuide.id,
                )

            // When / Then
            vm.state.test {
                assertEquals(GuideDetailUiState.Loading, awaitItem())
                val initial = assertIs<GuideDetailUiState.Loaded>(awaitItem())
                assertTrue(initial.isFavourite)

                vm.toggleFavourite()

                val flipped = assertIs<GuideDetailUiState.Loaded>(awaitItem())
                assertFalse(flipped.isFavourite)

                cancelAndIgnoreRemainingEvents()
            }

            advanceUntilIdle()
            assertEquals(1, unmark.invokeCount)
            assertEquals(fakeGuide.id, unmark.lastGuideId)
        }

    @Test
    fun `given mark fails when toggleFavourite is called then reverts the optimistic flip`() =
        runTestWithMainDispatcher {
            // Given
            val vm =
                GuideDetailViewModel(
                    getGuideById = FakeGetGuideByIdUseCase(Result.Success(fakeGuide)),
                    getUserMe = FakeGetUserMeUseCase(Result.Success(fakeUser)),
                    isGuideFavourite = FakeIsGuideFavouriteUseCase(Result.Success(false)),
                    markGuideFavourite =
                        FakeMarkGuideFavouriteUseCase(Result.Failure(DataError.Network(Exception("test error")))),
                    unmarkGuideFavourite = FakeUnmarkGuideFavouriteUseCase(),
                    guideId = fakeGuide.id,
                )

            // When / Then
            vm.state.test {
                assertEquals(GuideDetailUiState.Loading, awaitItem())
                val initial = assertIs<GuideDetailUiState.Loaded>(awaitItem())
                assertFalse(initial.isFavourite)

                vm.toggleFavourite()

                val flipped = assertIs<GuideDetailUiState.Loaded>(awaitItem())
                assertTrue(flipped.isFavourite)

                val reverted = assertIs<GuideDetailUiState.Loaded>(awaitItem())
                assertFalse(reverted.isFavourite)
            }
        }
}
