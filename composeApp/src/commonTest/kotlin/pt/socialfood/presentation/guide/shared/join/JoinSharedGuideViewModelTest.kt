package pt.socialfood.presentation.guide.shared.join

import app.cash.turbine.test
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.domain.usecase.guide.GetGuideByIdUseCase
import pt.socialfood.domain.usecase.guide.JoinGuideUseCase
import pt.socialfood.fakes.FakeGetGuideByIdUseCase
import pt.socialfood.fakes.FakeJoinGuideUseCase
import pt.socialfood.random.nextGuide
import pt.socialfood.random.nextString
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class JoinSharedGuideViewModelTest {
    private fun createViewModel(
        getGuideById: GetGuideByIdUseCase = FakeGetGuideByIdUseCase(Result.Success(Random.nextGuide())),
        joinGuide: JoinGuideUseCase = FakeJoinGuideUseCase(),
        guideId: String = Random.nextString(),
    ) = JoinSharedGuideViewModel(getGuideById, joinGuide, guideId)

    @Test
    fun `given the guide loads successfully when created then state becomes Loaded with the guide`() =
        runTestWithMainDispatcher {
            // Given
            val guide = Random.nextGuide()
            val getGuideById = FakeGetGuideByIdUseCase(Result.Success(guide))

            // When
            val vm = createViewModel(getGuideById = getGuideById)
            advanceUntilIdle()

            // Then
            val state = vm.state.value
            assertIs<JoinSharedGuideScreenUiState.Loaded>(state)
            assertEquals(guide, state.guide)
        }

    @Test
    fun `given the guide fails to load when created then state becomes Error`() = runTestWithMainDispatcher {
        // Given
        val error = DataError.Known(statusCode = 404, errorCode = ErrorCode.GUIDE_NOT_FOUND, message = "not found")
        val getGuideById = FakeGetGuideByIdUseCase(Result.Failure(error))

        // When
        val vm = createViewModel(getGuideById = getGuideById)
        advanceUntilIdle()

        // Then
        val state = vm.state.value
        assertIs<JoinSharedGuideScreenUiState.Error>(state)
        assertEquals(ErrorCode.GUIDE_NOT_FOUND, state.errorCode)
    }

    @Test
    fun `given joinGuide succeeds when onJoinClick is called then emits Joined with the guide id`() =
        runTestWithMainDispatcher {
            // Given
            val guide = Random.nextGuide()
            val getGuideById = FakeGetGuideByIdUseCase(Result.Success(guide))
            val joinGuide = FakeJoinGuideUseCase(result = Result.Success(guide))
            val vm = createViewModel(getGuideById = getGuideById, joinGuide = joinGuide, guideId = guide.id)
            advanceUntilIdle()

            // When / Then
            vm.events.test {
                vm.onJoinClick()
                val event = awaitItem()
                assertIs<JoinSharedGuideViewModel.UiEvent.Joined>(event)
                assertEquals(guide.id, event.guideId)
            }
            assertEquals(guide.id, joinGuide.lastGuideId)
        }

    @Test
    fun `given joinGuide fails when onJoinClick is called then state carries the join error`() =
        runTestWithMainDispatcher {
            // Given
            val guide = Random.nextGuide()
            val getGuideById = FakeGetGuideByIdUseCase(Result.Success(guide))
            val error = DataError.Known(statusCode = 404, errorCode = ErrorCode.GUIDE_NOT_FOUND, message = "invalid")
            val joinGuide = FakeJoinGuideUseCase(result = Result.Failure(error))
            val vm = createViewModel(getGuideById = getGuideById, joinGuide = joinGuide, guideId = guide.id)
            advanceUntilIdle()

            // When
            vm.onJoinClick()
            advanceUntilIdle()

            // Then
            val state = vm.state.value
            assertIs<JoinSharedGuideScreenUiState.Loaded>(state)
            assertEquals(false, state.isJoining)
            assertEquals(ErrorCode.GUIDE_NOT_FOUND, state.joinErrorCode)
        }
}
