package pt.socialfood.presentation.guide

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.domain.usecase.favourite.guide.MarkGuideFavouriteUseCase
import pt.socialfood.domain.usecase.favourite.guide.ObserveFavouriteGuideIdsUseCase
import pt.socialfood.domain.usecase.favourite.guide.UnmarkGuideFavouriteUseCase
import pt.socialfood.domain.usecase.guide.GetUserGuidesPagingUseCase
import pt.socialfood.domain.usecase.user.ObserveUserUseCase
import pt.socialfood.fakes.FakeGetUserGuidesPagingUseCase
import pt.socialfood.fakes.FakeMarkGuideFavouriteUseCase
import pt.socialfood.fakes.FakeObserveFavouriteGuideIdsUseCase
import pt.socialfood.fakes.FakeObserveUserUseCase
import pt.socialfood.fakes.FakeUnmarkGuideFavouriteUseCase
import pt.socialfood.presentation.guide.my.MyGuidesViewModel
import pt.socialfood.random.nextGuide
import pt.socialfood.random.nextString
import pt.socialfood.random.nextUser
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class MyGuidesViewModelTest {
    private fun createViewModel(
        getUserGuidesPaging: GetUserGuidesPagingUseCase = FakeGetUserGuidesPagingUseCase(),
        observeUser: ObserveUserUseCase = FakeObserveUserUseCase(Random.nextUser()),
        observeFavouriteGuideIds: ObserveFavouriteGuideIdsUseCase = FakeObserveFavouriteGuideIdsUseCase(),
        markGuideFavourite: MarkGuideFavouriteUseCase = FakeMarkGuideFavouriteUseCase(),
        unmarkGuideFavourite: UnmarkGuideFavouriteUseCase = FakeUnmarkGuideFavouriteUseCase(),
    ) = MyGuidesViewModel(
        getUserGuidesPaging,
        markGuideFavourite,
        unmarkGuideFavourite,
        observeUser,
        observeFavouriteGuideIds,
    )

    @Test
    fun `given the current user is available when guides is collected then getUserGuidesPaging is invoked`() =
        runTestWithMainDispatcher {
            // Given
            val user = Random.nextUser()
            val observeUser = FakeObserveUserUseCase(user)
            val getUserGuidesPaging = FakeGetUserGuidesPagingUseCase()
            val vm = createViewModel(getUserGuidesPaging = getUserGuidesPaging, observeUser = observeUser)

            // When
            val job = launch { vm.guides.collect {} }
            advanceUntilIdle()

            // Then
            assertEquals(1, getUserGuidesPaging.invokeCount)
            assertEquals(user.id, getUserGuidesPaging.lastUserId)
            job.cancel()
        }

    @Test
    fun `given no current user initially when observeUser later emits then guides is invoked with resolved userId`() =
        runTestWithMainDispatcher {
            // Given
            val user = Random.nextUser()
            val observeUser = FakeObserveUserUseCase(initial = null)
            val getUserGuidesPaging = FakeGetUserGuidesPagingUseCase()
            val vm = createViewModel(getUserGuidesPaging = getUserGuidesPaging, observeUser = observeUser)
            val job = launch { vm.guides.collect {} }
            advanceUntilIdle()

            // When
            observeUser.emit(user)
            advanceUntilIdle()

            // Then
            assertEquals(1, getUserGuidesPaging.invokeCount)
            assertEquals(user.id, getUserGuidesPaging.lastUserId)
            job.cancel()
        }

    @Test
    fun `given the current user changes when observeUser emits new user then guides is re-invoked with new user id`() =
        runTestWithMainDispatcher {
            // Given
            val observeUser = FakeObserveUserUseCase(Random.nextUser())
            val getUserGuidesPaging = FakeGetUserGuidesPagingUseCase()
            val vm = createViewModel(getUserGuidesPaging = getUserGuidesPaging, observeUser = observeUser)
            val job = launch { vm.guides.collect {} }
            advanceUntilIdle()

            // When
            val otherUser = Random.nextUser()
            observeUser.emit(otherUser)
            advanceUntilIdle()

            // Then
            assertEquals(otherUser.id, getUserGuidesPaging.lastUserId)
            job.cancel()
        }

    @Test
    fun `given the current user is observed then user reflects the emitted value`() = runTestWithMainDispatcher {
        // Given
        val user = Random.nextUser()
        val observeUser = FakeObserveUserUseCase(initial = user)

        // When / Then
        val vm = createViewModel(observeUser = observeUser)
        vm.user.test {
            awaitItem()
            assertEquals(user, awaitItem())
        }
    }

    @Test
    fun `given favourite ids are observed then favouriteGuideIds reflects them`() = runTestWithMainDispatcher {
        // Given
        val guideId = Random.nextString()
        val observeFavouriteGuideIds = FakeObserveFavouriteGuideIdsUseCase(initial = setOf(guideId))

        // When
        val vm = createViewModel(observeFavouriteGuideIds = observeFavouriteGuideIds)
        advanceUntilIdle()

        // Then
        assertEquals(setOf(guideId), vm.favouriteGuideIds.value)
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
            val target = Random.nextGuide()

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
            val guideId = Random.nextString()
            val observeFavouriteGuideIds = FakeObserveFavouriteGuideIdsUseCase(initial = setOf(guideId))
            val vm =
                createViewModel(
                    observeFavouriteGuideIds = observeFavouriteGuideIds,
                    markGuideFavourite = markGuideFavourite,
                    unmarkGuideFavourite = unmarkGuideFavourite,
                )
            val target = Random.nextGuide(id = guideId)
            advanceUntilIdle()

            // When
            vm.onToggleGuideFavourite(target)
            advanceUntilIdle()

            // Then
            assertEquals(guideId, unmarkGuideFavourite.lastGuideId)
            assertEquals(0, markGuideFavourite.invokeCount)
        }
}
