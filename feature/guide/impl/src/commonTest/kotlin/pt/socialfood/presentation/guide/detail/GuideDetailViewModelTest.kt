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
import pt.socialfood.fakes.FakeFavouritesGuidesRepository
import pt.socialfood.fakes.FakeGuidesRepository
import pt.socialfood.fakes.FakeUsersRepository
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
                    guidesRepository = FakeGuidesRepository(findByIdResult = Result.Success(fakeGuide)),
                    usersRepository = FakeUsersRepository(getUserMeResult = Result.Success(fakeUser)),
                    favouritesGuidesRepository = FakeFavouritesGuidesRepository(
                        isFavouriteResult = Result.Success(true),
                    ),
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
    fun `given guide is not a favourite when toggleFavourite is called then flips isFavourite and calls mark`() =
        runTestWithMainDispatcher {
            // Given
            val favouritesGuidesRepository = FakeFavouritesGuidesRepository(
                isFavouriteResult = Result.Success(false),
            )
            val vm =
                GuideDetailViewModel(
                    guidesRepository = FakeGuidesRepository(findByIdResult = Result.Success(fakeGuide)),
                    usersRepository = FakeUsersRepository(getUserMeResult = Result.Success(fakeUser)),
                    favouritesGuidesRepository = favouritesGuidesRepository,
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
            assertEquals(1, favouritesGuidesRepository.markInvokeCount)
            assertEquals(fakeGuide, favouritesGuidesRepository.lastMarkedGuide)
        }

    @Test
    fun `given guide is a favourite when toggleFavourite is called then flips isFavourite and calls unmark`() =
        runTestWithMainDispatcher {
            // Given
            val favouritesGuidesRepository = FakeFavouritesGuidesRepository(
                isFavouriteResult = Result.Success(true),
            )
            val vm =
                GuideDetailViewModel(
                    guidesRepository = FakeGuidesRepository(findByIdResult = Result.Success(fakeGuide)),
                    usersRepository = FakeUsersRepository(getUserMeResult = Result.Success(fakeUser)),
                    favouritesGuidesRepository = favouritesGuidesRepository,
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
            assertEquals(1, favouritesGuidesRepository.unmarkInvokeCount)
            assertEquals(fakeGuide.id, favouritesGuidesRepository.lastUnmarkedGuideId)
        }

    @Test
    fun `given mark fails when toggleFavourite is called then reverts the optimistic flip`() =
        runTestWithMainDispatcher {
            // Given
            val vm =
                GuideDetailViewModel(
                    guidesRepository = FakeGuidesRepository(findByIdResult = Result.Success(fakeGuide)),
                    usersRepository = FakeUsersRepository(getUserMeResult = Result.Success(fakeUser)),
                    favouritesGuidesRepository = FakeFavouritesGuidesRepository(
                        isFavouriteResult = Result.Success(false),
                        markResult = Result.Failure(DataError.Network(Exception("test error"))),
                    ),
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

    @Test
    fun `given leaving succeeds when onLeaveGuide is called then sets isLeaving and emits GuideLeft`() =
        runTestWithMainDispatcher {
            // Given
            val guidesRepository = FakeGuidesRepository(
                findByIdResult = Result.Success(fakeGuide),
                leaveGuideResult = Result.Success(true),
            )
            val vm =
                GuideDetailViewModel(
                    guidesRepository = guidesRepository,
                    usersRepository = FakeUsersRepository(getUserMeResult = Result.Success(fakeUser)),
                    favouritesGuidesRepository = FakeFavouritesGuidesRepository(
                        isFavouriteResult = Result.Success(false),
                    ),
                    guideId = fakeGuide.id,
                )
            advanceUntilIdle()

            // When / Then
            vm.events.test {
                vm.onLeaveGuide()

                val leaving = assertIs<GuideDetailUiState.Loaded>(vm.state.value)
                assertTrue(leaving.isLeaving)

                assertEquals(GuideDetailViewModel.UiEvent.GuideLeft, awaitItem())
            }

            assertEquals(1, guidesRepository.leaveGuideInvokeCount)
            assertEquals(fakeGuide.id, guidesRepository.lastLeaveGuideId)
        }

    @Test
    fun `given leaving fails when onLeaveGuide is called then reverts isLeaving`() = runTestWithMainDispatcher {
        // Given
        val error = DataError.Network(Exception("test error"))
        val vm =
            GuideDetailViewModel(
                guidesRepository = FakeGuidesRepository(
                    findByIdResult = Result.Success(fakeGuide),
                    leaveGuideResult = Result.Failure(error),
                ),
                usersRepository = FakeUsersRepository(getUserMeResult = Result.Success(fakeUser)),
                favouritesGuidesRepository = FakeFavouritesGuidesRepository(
                    isFavouriteResult = Result.Success(false),
                ),
                guideId = fakeGuide.id,
            )
        advanceUntilIdle()

        // When
        vm.onLeaveGuide()
        val leaving = assertIs<GuideDetailUiState.Loaded>(vm.state.value)
        assertTrue(leaving.isLeaving)

        advanceUntilIdle()

        // Then
        val reverted = assertIs<GuideDetailUiState.Loaded>(vm.state.value)
        assertFalse(reverted.isLeaving)
    }
}
