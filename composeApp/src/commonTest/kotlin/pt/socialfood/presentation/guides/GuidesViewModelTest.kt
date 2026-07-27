package pt.socialfood.presentation.guides

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.domain.model.User
import pt.socialfood.fakes.FakeGetGuidesPagingUseCase
import pt.socialfood.fakes.FakeObserveUserUseCase
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class GuidesViewModelTest {

    private val fakeUser = User(id = "user-1", email = "user@test.com", name = "Test User")

    @Test
    fun `given selectedTab is 0 when guides is collected then getGuidesPaging is invoked with userId null`() = runTestWithMainDispatcher {
        // Given
        val getGuidesPaging = FakeGetGuidesPagingUseCase()
        val vm = GuidesViewModel(getGuidesPaging, FakeObserveUserUseCase(fakeUser))

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
        val vm = GuidesViewModel(getGuidesPaging, observeUser)
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
        val vm = GuidesViewModel(getGuidesPaging, FakeObserveUserUseCase(fakeUser))
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
        val vm = GuidesViewModel(getGuidesPaging, observeUser)
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
}
