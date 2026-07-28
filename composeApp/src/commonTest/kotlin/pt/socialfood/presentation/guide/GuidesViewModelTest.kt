package pt.socialfood.presentation.guide

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.domain.model.User
import pt.socialfood.domain.use_case.favourite.guide.MarkGuideFavouriteUseCase
import pt.socialfood.domain.use_case.favourite.guide.ObserveFavouriteGuideIdsUseCase
import pt.socialfood.domain.use_case.favourite.guide.UnmarkGuideFavouriteUseCase
import pt.socialfood.domain.use_case.guide.GetGuidesPagingUseCase
import pt.socialfood.domain.use_case.user.ObserveUserUseCase
import pt.socialfood.fakes.FakeGetGuidesPagingUseCase
import pt.socialfood.fakes.FakeMarkGuideFavouriteUseCase
import pt.socialfood.fakes.FakeObserveFavouriteGuideIdsUseCase
import pt.socialfood.fakes.FakeObserveUserUseCase
import pt.socialfood.fakes.FakeUnmarkGuideFavouriteUseCase
import pt.socialfood.presentation.guide.list.GuidesViewModel
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class GuidesViewModelTest {

    private val fakeUser = User(id = "user-1", email = "user@test.com", name = "Test User")

    private fun guide(id: String) = Guide(
        id = id,
        name = "Guide $id",
        description = "Description $id",
        visibility = GuideVisibility.PUBLIC,
        author = Author(id = "author-1", name = "Author"),
        numberOfRestaurant = 0,
    )

    private fun createViewModel(
        getGuidesPaging: GetGuidesPagingUseCase = FakeGetGuidesPagingUseCase(),
        observeUser: ObserveUserUseCase = FakeObserveUserUseCase(fakeUser),
        observeFavouriteGuideIds: ObserveFavouriteGuideIdsUseCase = FakeObserveFavouriteGuideIdsUseCase(),
        markGuideFavourite: MarkGuideFavouriteUseCase = FakeMarkGuideFavouriteUseCase(),
        unmarkGuideFavourite: UnmarkGuideFavouriteUseCase = FakeUnmarkGuideFavouriteUseCase(),
    ) = GuidesViewModel(getGuidesPaging, observeUser, observeFavouriteGuideIds, markGuideFavourite, unmarkGuideFavourite)

    @Test
    fun `given selectedTab is 0 when guides is collected then getGuidesPaging is invoked with userId null`() = runTestWithMainDispatcher {
        // Given
        val getGuidesPaging = FakeGetGuidesPagingUseCase()
        val vm = createViewModel(getGuidesPaging = getGuidesPaging)

        // When
        val job = launch { vm.guides.collect {} }
        advanceUntilIdle()

        // Then
        assertEquals(1, getGuidesPaging.invokeCount)
        assertNull(getGuidesPaging.lastUserId)
        job.cancel()
    }

    @Test
    fun `given onTabSelected 1 is called before observeUser emits when observeUser later emits then guides is re-invoked with the resolved userId`() = runTestWithMainDispatcher {
        // Given
        val observeUser = FakeObserveUserUseCase(initial = null)
        val getGuidesPaging = FakeGetGuidesPagingUseCase()
        val vm = createViewModel(getGuidesPaging = getGuidesPaging, observeUser = observeUser)
        val job = launch { vm.guides.collect {} }
        advanceUntilIdle()

        // When
        vm.onTabSelected(1)
        advanceUntilIdle()
        observeUser.emit(fakeUser)
        advanceUntilIdle()

        // Then
        assertEquals(fakeUser.id, getGuidesPaging.lastUserId)
        job.cancel()
    }

    @Test
    fun `given onTabSelected is called with the same tab twice then getGuidesPaging is not invoked a second time`() = runTestWithMainDispatcher {
        // Given
        val getGuidesPaging = FakeGetGuidesPagingUseCase()
        val vm = createViewModel(getGuidesPaging = getGuidesPaging)
        val job = launch { vm.guides.collect {} }
        advanceUntilIdle()
        val countAfterInit = getGuidesPaging.invokeCount

        // When
        vm.onTabSelected(0)
        advanceUntilIdle()

        // Then
        assertEquals(countAfterInit, getGuidesPaging.invokeCount)
        job.cancel()
    }

    @Test
    fun `given current user changes when observeUser emits a new user then guides is re-invoked with the new user id`() = runTestWithMainDispatcher {
        // Given
        val observeUser = FakeObserveUserUseCase(fakeUser)
        val getGuidesPaging = FakeGetGuidesPagingUseCase()
        val vm = createViewModel(getGuidesPaging = getGuidesPaging, observeUser = observeUser)
        val job = launch { vm.guides.collect {} }
        advanceUntilIdle()
        vm.onTabSelected(1)
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
    fun `given a guide is not favourited when onToggleGuideFavourite is called then markGuideFavourite is invoked`() = runTestWithMainDispatcher {
        // Given
        val markGuideFavourite = FakeMarkGuideFavouriteUseCase()
        val unmarkGuideFavourite = FakeUnmarkGuideFavouriteUseCase()
        val vm = createViewModel(markGuideFavourite = markGuideFavourite, unmarkGuideFavourite = unmarkGuideFavourite)
        val target = guide("g1")

        // When
        vm.onToggleGuideFavourite(target)
        advanceUntilIdle()

        // Then
        assertEquals(target, markGuideFavourite.lastGuide)
        assertEquals(0, unmarkGuideFavourite.invokeCount)
    }

    @Test
    fun `given a guide is already favourited when onToggleGuideFavourite is called then unmarkGuideFavourite is invoked`() = runTestWithMainDispatcher {
        // Given
        val markGuideFavourite = FakeMarkGuideFavouriteUseCase()
        val unmarkGuideFavourite = FakeUnmarkGuideFavouriteUseCase()
        val observeFavouriteGuideIds = FakeObserveFavouriteGuideIdsUseCase(initial = setOf("g1"))
        val vm = createViewModel(
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
