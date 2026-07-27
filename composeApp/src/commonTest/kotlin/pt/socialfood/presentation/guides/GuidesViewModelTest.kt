package pt.socialfood.presentation.guides

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.core.Result
import pt.socialfood.domain.error.ErrorEntity
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.domain.model.PagedGuides
import pt.socialfood.domain.model.User
import pt.socialfood.fakes.FakeFindGuidesUseCase
import pt.socialfood.fakes.FakeGetGuidesPagingUseCase
import pt.socialfood.fakes.FakeGetUserMeUseCase
import pt.socialfood.fakes.FakeObserveUserUseCase
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GuidesViewModelTest {

    private val fakeUser = User(id = "user-1", email = "user@test.com", name = "Test User")

    private fun guide(id: String) = Guide(
        id = id,
        name = "Guide $id",
        description = "",
        visibility = GuideVisibility.PUBLIC,
        author = Author(id = "author-1", name = "Author"),
        numberOfRestaurant = 0,
    )

    @Test
    fun `given guides exist when created then loads first page into Loaded state`() = runTestWithMainDispatcher {
        // Given
        val findGuides = FakeFindGuidesUseCase { page, _ ->
            Result.Success(PagedGuides(guides = listOf(guide("g1")), page = page, total = 1, hasMore = false))
        }
        val getUserMe = FakeGetUserMeUseCase(Result.Success(fakeUser))

        // When
        val vm = GuidesViewModel(findGuides, getUserMe, FakeGetGuidesPagingUseCase(), FakeObserveUserUseCase(fakeUser))
        advanceUntilIdle()

        // Then
        val state = assertIs<GuidesUiState.Loaded>(vm.state.value)
        assertEquals(1, state.guides.size)
        assertEquals("g1", state.guides.first().id)
    }

    @Test
    fun `given use case fails when created then state is Error`() = runTestWithMainDispatcher {
        // Given
        val findGuides = FakeFindGuidesUseCase { _, _ -> Result.Error(ErrorEntity.Unknown) }
        val getUserMe = FakeGetUserMeUseCase(Result.Success(fakeUser))

        // When
        val vm = GuidesViewModel(findGuides, getUserMe, FakeGetGuidesPagingUseCase(), FakeObserveUserUseCase(fakeUser))
        advanceUntilIdle()

        // Then
        assertIs<GuidesUiState.Error>(vm.state.value)
    }

    @Test
    fun `given default All tab when created then loads guides with null userId`() = runTestWithMainDispatcher {
        // Given
        val findGuides = FakeFindGuidesUseCase()
        val getUserMe = FakeGetUserMeUseCase(Result.Success(fakeUser))

        // When
        val vm = GuidesViewModel(findGuides, getUserMe, FakeGetGuidesPagingUseCase(), FakeObserveUserUseCase(fakeUser))
        advanceUntilIdle()

        // Then
        assertNull(findGuides.lastUserId)
    }

    @Test
    fun `given My Guides tab selected when onTabSelected is called then reloads filtered by current user id`() = runTestWithMainDispatcher {
        // Given
        val findGuides = FakeFindGuidesUseCase()
        val getUserMe = FakeGetUserMeUseCase(Result.Success(fakeUser))
        val vm = GuidesViewModel(findGuides, getUserMe, FakeGetGuidesPagingUseCase(), FakeObserveUserUseCase(fakeUser))
        advanceUntilIdle()

        // When
        vm.onTabSelected(1)
        advanceUntilIdle()

        // Then
        assertEquals(fakeUser.id, findGuides.lastUserId)
        assertIs<GuidesUiState.Loaded>(vm.state.value)
    }

    @Test
    fun `given My Guides tab active when All tab is selected then reloads with null userId`() = runTestWithMainDispatcher {
        // Given
        val findGuides = FakeFindGuidesUseCase()
        val getUserMe = FakeGetUserMeUseCase(Result.Success(fakeUser))
        val vm = GuidesViewModel(findGuides, getUserMe, FakeGetGuidesPagingUseCase(), FakeObserveUserUseCase(fakeUser))
        advanceUntilIdle()
        vm.onTabSelected(1)
        advanceUntilIdle()

        // When
        vm.onTabSelected(0)
        advanceUntilIdle()

        // Then
        assertNull(findGuides.lastUserId)
    }

    @Test
    fun `given tab already selected when onTabSelected is called again then does not reload`() = runTestWithMainDispatcher {
        // Given
        val findGuides = FakeFindGuidesUseCase()
        val getUserMe = FakeGetUserMeUseCase(Result.Success(fakeUser))
        val vm = GuidesViewModel(findGuides, getUserMe, FakeGetGuidesPagingUseCase(), FakeObserveUserUseCase(fakeUser))
        advanceUntilIdle()
        val countAfterInit = findGuides.invokeCount

        // When
        vm.onTabSelected(0)
        advanceUntilIdle()

        // Then
        assertEquals(countAfterInit, findGuides.invokeCount)
    }

    @Test
    fun `given more pages available when loadMore is called then appends guides and updates hasMore`() = runTestWithMainDispatcher {
        // Given
        val findGuides = FakeFindGuidesUseCase { page, _ ->
            if (page == 1) {
                Result.Success(PagedGuides(guides = listOf(guide("g1")), page = 1, total = 2, hasMore = true))
            } else {
                Result.Success(PagedGuides(guides = listOf(guide("g2")), page = 2, total = 2, hasMore = false))
            }
        }
        val getUserMe = FakeGetUserMeUseCase(Result.Success(fakeUser))
        val vm = GuidesViewModel(findGuides, getUserMe, FakeGetGuidesPagingUseCase(), FakeObserveUserUseCase(fakeUser))
        advanceUntilIdle()

        // When
        vm.loadMore()
        advanceUntilIdle()

        // Then
        val state = assertIs<GuidesUiState.Loaded>(vm.state.value)
        assertEquals(listOf("g1", "g2"), state.guides.map { it.id })
        assertTrue(!state.hasMore)
    }

    @Test
    fun `given refresh is called then reloads first page and clears isRefreshing`() = runTestWithMainDispatcher {
        // Given
        val findGuides = FakeFindGuidesUseCase { page, _ ->
            Result.Success(PagedGuides(guides = listOf(guide("g1")), page = page, total = 1, hasMore = false))
        }
        val getUserMe = FakeGetUserMeUseCase(Result.Success(fakeUser))
        val vm = GuidesViewModel(findGuides, getUserMe, FakeGetGuidesPagingUseCase(), FakeObserveUserUseCase(fakeUser))
        advanceUntilIdle()

        // When
        vm.refresh()
        advanceUntilIdle()

        // Then
        assertEquals(false, vm.isRefreshing.value)
        assertIs<GuidesUiState.Loaded>(vm.state.value)
    }

    @Test
    fun `given My Guides tab active when refresh is called then keeps filtering by current user id`() = runTestWithMainDispatcher {
        // Given
        val findGuides = FakeFindGuidesUseCase()
        val getUserMe = FakeGetUserMeUseCase(Result.Success(fakeUser))
        val vm = GuidesViewModel(findGuides, getUserMe, FakeGetGuidesPagingUseCase(), FakeObserveUserUseCase(fakeUser))
        advanceUntilIdle()
        vm.onTabSelected(1)
        advanceUntilIdle()

        // When
        vm.refresh()
        advanceUntilIdle()

        // Then
        assertEquals(fakeUser.id, findGuides.lastUserId)
    }

    @Test
    fun `given selectedTab is 0 when guides is collected then getGuidesPaging is invoked with userId null`() = runTestWithMainDispatcher {
        // Given
        val findGuides = FakeFindGuidesUseCase()
        val getUserMe = FakeGetUserMeUseCase(Result.Success(fakeUser))
        val getGuidesPaging = FakeGetGuidesPagingUseCase()
        val vm = GuidesViewModel(findGuides, getUserMe, getGuidesPaging, FakeObserveUserUseCase(fakeUser))

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
        val findGuides = FakeFindGuidesUseCase()
        val getUserMe = FakeGetUserMeUseCase(Result.Success(fakeUser))
        val observeUser = FakeObserveUserUseCase(initial = null)
        val getGuidesPaging = FakeGetGuidesPagingUseCase()
        val vm = GuidesViewModel(findGuides, getUserMe, getGuidesPaging, observeUser)
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
        val findGuides = FakeFindGuidesUseCase()
        val getUserMe = FakeGetUserMeUseCase(Result.Success(fakeUser))
        val getGuidesPaging = FakeGetGuidesPagingUseCase()
        val vm = GuidesViewModel(findGuides, getUserMe, getGuidesPaging, FakeObserveUserUseCase(fakeUser))
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
        val findGuides = FakeFindGuidesUseCase()
        val getUserMe = FakeGetUserMeUseCase(Result.Success(fakeUser))
        val observeUser = FakeObserveUserUseCase(fakeUser)
        val getGuidesPaging = FakeGetGuidesPagingUseCase()
        val vm = GuidesViewModel(findGuides, getUserMe, getGuidesPaging, observeUser)
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
