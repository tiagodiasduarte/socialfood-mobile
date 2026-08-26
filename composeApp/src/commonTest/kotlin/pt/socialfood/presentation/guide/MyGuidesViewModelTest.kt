package pt.socialfood.presentation.guide

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.domain.model.User
import pt.socialfood.domain.usecase.favourite.guide.MarkGuideFavouriteUseCase
import pt.socialfood.domain.usecase.favourite.guide.ObserveFavouriteGuideIdsUseCase
import pt.socialfood.domain.usecase.favourite.guide.UnmarkGuideFavouriteUseCase
import pt.socialfood.domain.usecase.guide.GetGuidesPagingUseCase
import pt.socialfood.domain.usecase.user.ObserveUserUseCase
import pt.socialfood.fakes.FakeGetGuidesPagingUseCase
import pt.socialfood.fakes.FakeMarkGuideFavouriteUseCase
import pt.socialfood.fakes.FakeObserveFavouriteGuideIdsUseCase
import pt.socialfood.fakes.FakeObserveUserUseCase
import pt.socialfood.fakes.FakeUnmarkGuideFavouriteUseCase
import pt.socialfood.presentation.guide.my.MyGuidesViewModel
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class MyGuidesViewModelTest {
    private val fakeUser = User(id = "user-1", email = "user@test.com", name = "Test User", username = "testuser")

    private fun guide(id: String) = Guide(
        id = id,
        name = "Guide $id",
        description = "Description $id",
        visibility = GuideVisibility.PUBLIC,
        author = Author(id = "author-1", name = "Author", username = "author"),
        numberOfRestaurant = 0,
    )

    private fun createViewModel(
        getGuidesPaging: GetGuidesPagingUseCase = FakeGetGuidesPagingUseCase(),
        observeUser: ObserveUserUseCase = FakeObserveUserUseCase(fakeUser),
        observeFavouriteGuideIds: ObserveFavouriteGuideIdsUseCase = FakeObserveFavouriteGuideIdsUseCase(),
        markGuideFavourite: MarkGuideFavouriteUseCase = FakeMarkGuideFavouriteUseCase(),
        unmarkGuideFavourite: UnmarkGuideFavouriteUseCase = FakeUnmarkGuideFavouriteUseCase(),
    ) = MyGuidesViewModel(
        getGuidesPaging,
        markGuideFavourite,
        unmarkGuideFavourite,
        observeUser,
        observeFavouriteGuideIds,
    )

    @Test
    fun `given the current user is available when guides is collected then getGuidesPaging is invoked with user id`() =
        runTestWithMainDispatcher {
            // Given
            val getGuidesPaging = FakeGetGuidesPagingUseCase()
            val vm = createViewModel(getGuidesPaging = getGuidesPaging)

            // When
            val job = launch { vm.guides.collect {} }
            advanceUntilIdle()

            // Then
            assertEquals(1, getGuidesPaging.invokeCount)
            assertEquals(fakeUser.id, getGuidesPaging.lastUserId)
            job.cancel()
        }

    @Test
    fun `given no current user initially when observeUser later emits then guides is invoked with resolved userId`() =
        runTestWithMainDispatcher {
            // Given
            val observeUser = FakeObserveUserUseCase(initial = null)
            val getGuidesPaging = FakeGetGuidesPagingUseCase()
            val vm = createViewModel(getGuidesPaging = getGuidesPaging, observeUser = observeUser)
            val job = launch { vm.guides.collect {} }
            advanceUntilIdle()

            // When
            observeUser.emit(fakeUser)
            advanceUntilIdle()

            // Then
            assertEquals(1, getGuidesPaging.invokeCount)
            assertEquals(fakeUser.id, getGuidesPaging.lastUserId)
            job.cancel()
        }

    @Test
    fun `given the current user changes when observeUser emits new user then guides is re-invoked with new user id`() =
        runTestWithMainDispatcher {
            // Given
            val observeUser = FakeObserveUserUseCase(fakeUser)
            val getGuidesPaging = FakeGetGuidesPagingUseCase()
            val vm = createViewModel(getGuidesPaging = getGuidesPaging, observeUser = observeUser)
            val job = launch { vm.guides.collect {} }
            advanceUntilIdle()

            // When
            val otherUser = fakeUser.copy(id = "user-2")
            observeUser.emit(otherUser)
            advanceUntilIdle()

            // Then
            assertEquals(otherUser.id, getGuidesPaging.lastUserId)
            job.cancel()
        }

    @Test
    fun `given the current user is observed then user reflects the emitted value`() = runTestWithMainDispatcher {
        // Given
        val observeUser = FakeObserveUserUseCase(initial = fakeUser)

        // When / Then
        val vm = createViewModel(observeUser = observeUser)
        vm.user.test {
            awaitItem()
            assertEquals(fakeUser, awaitItem())
        }
    }

    @Test
    fun `given favourite ids are observed then favouriteGuideIds reflects them`() = runTestWithMainDispatcher {
        // Given
        val observeFavouriteGuideIds = FakeObserveFavouriteGuideIdsUseCase(initial = setOf("g1"))

        // When
        val vm = createViewModel(observeFavouriteGuideIds = observeFavouriteGuideIds)
        advanceUntilIdle()

        // Then
        assertEquals(setOf("g1"), vm.favouriteGuideIds.value)
    }

    @Test
    fun `given a guide is not favourited when onToggleGuideFavourite is called then markGuideFavourite is invoked`() =
        runTestWithMainDispatcher {
            // Given
            val markGuideFavourite = FakeMarkGuideFavouriteUseCase()
            val unmarkGuideFavourite = FakeUnmarkGuideFavouriteUseCase()
            val vm =
                createViewModel(
                    markGuideFavourite = markGuideFavourite,
                    unmarkGuideFavourite = unmarkGuideFavourite,
                )
            val target = guide("g1")

            // When
            vm.onToggleGuideFavourite(target)
            advanceUntilIdle()

            // Then
            assertEquals(target, markGuideFavourite.lastGuide)
            assertEquals(0, unmarkGuideFavourite.invokeCount)
        }

    @Test
    fun `given a favourited guide when onToggleGuideFavourite is called then unmarkGuideFavourite is invoked`() =
        runTestWithMainDispatcher {
            // Given
            val markGuideFavourite = FakeMarkGuideFavouriteUseCase()
            val unmarkGuideFavourite = FakeUnmarkGuideFavouriteUseCase()
            val observeFavouriteGuideIds = FakeObserveFavouriteGuideIdsUseCase(initial = setOf("g1"))
            val vm =
                createViewModel(
                    observeFavouriteGuideIds = observeFavouriteGuideIds,
                    markGuideFavourite = markGuideFavourite,
                    unmarkGuideFavourite = unmarkGuideFavourite,
                )
            val target = guide("g1")
            advanceUntilIdle()

            // When
            vm.onToggleGuideFavourite(target)
            advanceUntilIdle()

            // Then
            assertEquals("g1", unmarkGuideFavourite.lastGuideId)
            assertEquals(0, markGuideFavourite.invokeCount)
        }
}
