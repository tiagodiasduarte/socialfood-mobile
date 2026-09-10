package pt.socialfood.presentation.guide

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.domain.usecase.favourite.guide.MarkGuideFavouriteUseCase
import pt.socialfood.domain.usecase.favourite.guide.ObserveFavouriteGuideIdsUseCase
import pt.socialfood.domain.usecase.favourite.guide.UnmarkGuideFavouriteUseCase
import pt.socialfood.domain.usecase.guide.GetGuideBySharedCodeUseCase
import pt.socialfood.domain.usecase.guide.GetUserJoinedGuidesPagingUseCase
import pt.socialfood.domain.usecase.guide.JoinGuideUseCase
import pt.socialfood.domain.usecase.user.ObserveUserUseCase
import pt.socialfood.fakes.FakeGetGuideBySharedCodeUseCase
import pt.socialfood.fakes.FakeGetUserJoinedGuidesPagingUseCase
import pt.socialfood.fakes.FakeJoinGuideUseCase
import pt.socialfood.fakes.FakeMarkGuideFavouriteUseCase
import pt.socialfood.fakes.FakeObserveFavouriteGuideIdsUseCase
import pt.socialfood.fakes.FakeObserveUserUseCase
import pt.socialfood.fakes.FakeUnmarkGuideFavouriteUseCase
import pt.socialfood.presentation.guide.shared.SharedGuidesViewModel
import pt.socialfood.presentation.guide.shared.join.JoinSharedGuideDialogUiState
import pt.socialfood.random.nextGuide
import pt.socialfood.random.nextString
import pt.socialfood.random.nextUser
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class SharedGuidesViewModelTest {
    private fun createViewModel(
        getUserJoinedGuidesPaging: GetUserJoinedGuidesPagingUseCase = FakeGetUserJoinedGuidesPagingUseCase(),
        getGuideBySharedCode: GetGuideBySharedCodeUseCase = FakeGetGuideBySharedCodeUseCase(),
        joinGuide: JoinGuideUseCase = FakeJoinGuideUseCase(),
        observeUser: ObserveUserUseCase = FakeObserveUserUseCase(Random.nextUser()),
        observeFavouriteGuideIds: ObserveFavouriteGuideIdsUseCase = FakeObserveFavouriteGuideIdsUseCase(),
        markGuideFavourite: MarkGuideFavouriteUseCase = FakeMarkGuideFavouriteUseCase(),
        unmarkGuideFavourite: UnmarkGuideFavouriteUseCase = FakeUnmarkGuideFavouriteUseCase(),
    ) = SharedGuidesViewModel(
        getUserJoinedGuidesPaging,
        getGuideBySharedCode,
        joinGuide,
        markGuideFavourite,
        unmarkGuideFavourite,
        observeUser,
        observeFavouriteGuideIds,
    )

    @Test
    fun `given the current user is available when guides is collected then getUserJoinedGuidesPaging is invoked`() =
        runTestWithMainDispatcher {
            // Given
            val user = Random.nextUser()
            val observeUser = FakeObserveUserUseCase(user)
            val getUserJoinedGuidesPaging = FakeGetUserJoinedGuidesPagingUseCase()
            val vm = createViewModel(getUserJoinedGuidesPaging = getUserJoinedGuidesPaging, observeUser = observeUser)

            // When
            val job = launch { vm.guides.collect {} }
            advanceUntilIdle()

            // Then
            assertEquals(1, getUserJoinedGuidesPaging.invokeCount)
            assertEquals(user.id, getUserJoinedGuidesPaging.lastUserId)
            job.cancel()
        }

    @Test
    fun `given no current user initially when observeUser later emits then guides is invoked with resolved userId`() =
        runTestWithMainDispatcher {
            // Given
            val user = Random.nextUser()
            val observeUser = FakeObserveUserUseCase(initial = null)
            val getUserJoinedGuidesPaging = FakeGetUserJoinedGuidesPagingUseCase()
            val vm = createViewModel(getUserJoinedGuidesPaging = getUserJoinedGuidesPaging, observeUser = observeUser)
            val job = launch { vm.guides.collect {} }
            advanceUntilIdle()

            // When
            observeUser.emit(user)
            advanceUntilIdle()

            // Then
            assertEquals(1, getUserJoinedGuidesPaging.invokeCount)
            assertEquals(user.id, getUserJoinedGuidesPaging.lastUserId)
            job.cancel()
        }

    @Test
    fun `given the current user changes when observeUser emits new user then guides is re-invoked with new user id`() =
        runTestWithMainDispatcher {
            // Given
            val observeUser = FakeObserveUserUseCase(Random.nextUser())
            val getUserJoinedGuidesPaging = FakeGetUserJoinedGuidesPagingUseCase()
            val vm = createViewModel(getUserJoinedGuidesPaging = getUserJoinedGuidesPaging, observeUser = observeUser)
            val job = launch { vm.guides.collect {} }
            advanceUntilIdle()

            // When
            val otherUser = Random.nextUser()
            observeUser.emit(otherUser)
            advanceUntilIdle()

            // Then
            assertEquals(otherUser.id, getUserJoinedGuidesPaging.lastUserId)
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

    @Test
    fun `given getGuideBySharedCode succeeds when onJoinGuide is called then guideToJoin holds the resolved guide`() =
        runTestWithMainDispatcher {
            // Given
            val guide = Random.nextGuide()
            val getGuideBySharedCode = FakeGetGuideBySharedCodeUseCase(result = Result.Success(guide))
            val vm = createViewModel(getGuideBySharedCode = getGuideBySharedCode)
            val code = Random.nextString()

            // When
            vm.onJoinGuide(code)
            advanceUntilIdle()

            // Then
            assertEquals(code, getGuideBySharedCode.lastCode)
            assertEquals(guide, vm.guideToJoin.value?.guide)
            assertEquals(JoinSharedGuideDialogUiState.Idle, vm.joinGuideState.value)
        }

    @Test
    fun `given getGuideBySharedCode fails when onJoinGuide is called then joinGuideState becomes Error`() =
        runTestWithMainDispatcher {
            // Given
            val error = DataError.Known(statusCode = 404, errorCode = ErrorCode.GUIDE_NOT_FOUND, message = "invalid")
            val getGuideBySharedCode = FakeGetGuideBySharedCodeUseCase(result = Result.Failure(error))
            val vm = createViewModel(getGuideBySharedCode = getGuideBySharedCode)

            // When
            vm.onJoinGuide(Random.nextString())
            advanceUntilIdle()

            // Then
            val state = vm.joinGuideState.value
            assertIs<JoinSharedGuideDialogUiState.Error>(state)
            assertEquals(ErrorCode.GUIDE_NOT_FOUND, state.errorCode)
        }

    @Test
    fun `given an Error state when onDismissJoinGuideError is called then joinGuideState resets to Idle`() =
        runTestWithMainDispatcher {
            // Given
            val error = DataError.Known(statusCode = 404, errorCode = ErrorCode.GUIDE_NOT_FOUND, message = "invalid")
            val getGuideBySharedCode = FakeGetGuideBySharedCodeUseCase(result = Result.Failure(error))
            val vm = createViewModel(getGuideBySharedCode = getGuideBySharedCode)
            vm.onJoinGuide(Random.nextString())
            advanceUntilIdle()

            // When
            vm.onDismissJoinGuideError()

            // Then
            assertEquals(JoinSharedGuideDialogUiState.Idle, vm.joinGuideState.value)
        }

    @Test
    fun `given a resolved guide and joinGuide succeeds when onJoinGuideConfirm is called then emits GuideJoined`() =
        runTestWithMainDispatcher {
            // Given
            val guide = Random.nextGuide()
            val getGuideBySharedCode = FakeGetGuideBySharedCodeUseCase(result = Result.Success(guide))
            val joinGuide = FakeJoinGuideUseCase(result = Result.Success(guide))
            val vm = createViewModel(getGuideBySharedCode = getGuideBySharedCode, joinGuide = joinGuide)
            val code = Random.nextString()
            vm.onJoinGuide(code)
            advanceUntilIdle()

            // When / Then
            vm.events.test {
                vm.onJoinGuideConfirm()
                val event = awaitItem()
                assertIs<SharedGuidesViewModel.UiEvent.GuideJoined>(event)
                assertEquals(guide.id, event.guideId)
            }
            assertEquals(guide.id, joinGuide.lastGuideId)
            assertEquals(code, joinGuide.lastCode)
            assertEquals(null, vm.guideToJoin.value)
        }

    @Test
    fun `given a resolved guide and joinGuide fails when onJoinGuideConfirm is called then card carries the error`() =
        runTestWithMainDispatcher {
            // Given
            val guide = Random.nextGuide()
            val getGuideBySharedCode = FakeGetGuideBySharedCodeUseCase(result = Result.Success(guide))
            val error = DataError.Known(statusCode = 404, errorCode = ErrorCode.GUIDE_NOT_FOUND, message = "invalid")
            val joinGuide = FakeJoinGuideUseCase(result = Result.Failure(error))
            val vm = createViewModel(getGuideBySharedCode = getGuideBySharedCode, joinGuide = joinGuide)
            vm.onJoinGuide(Random.nextString())
            advanceUntilIdle()

            // When
            vm.onJoinGuideConfirm()
            advanceUntilIdle()

            // Then
            val state = vm.guideToJoin.value
            assertEquals(false, state?.isJoining)
            assertEquals(ErrorCode.GUIDE_NOT_FOUND, state?.joinErrorCode)
        }

    @Test
    fun `given a resolved guide when onDismissJoinGuideCard is called then guideToJoin is cleared`() =
        runTestWithMainDispatcher {
            // Given
            val guide = Random.nextGuide()
            val getGuideBySharedCode = FakeGetGuideBySharedCodeUseCase(result = Result.Success(guide))
            val vm = createViewModel(getGuideBySharedCode = getGuideBySharedCode)
            vm.onJoinGuide(Random.nextString())
            advanceUntilIdle()

            // When
            vm.onDismissJoinGuideCard()

            // Then
            assertEquals(null, vm.guideToJoin.value)
        }
}
