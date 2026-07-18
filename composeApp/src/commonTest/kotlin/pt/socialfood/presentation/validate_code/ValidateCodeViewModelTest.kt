package pt.socialfood.presentation.validate_code

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.fakes.FakeResendVerificationCodeUseCase
import pt.socialfood.fakes.FakeRestartSignUpUseCase
import pt.socialfood.fakes.FakeValidateCodeUseCase
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ValidateCodeViewModelTest {

    @Test
    fun `given a fake restart sign up use case when onRestartSignUp is called then use case is invoked and state becomes RestartSignUp`() = runTestWithMainDispatcher {
        // Given
        val fakeRestartSignUp = FakeRestartSignUpUseCase()
        val viewModel = ValidateCodeViewModel(
            validateCode = FakeValidateCodeUseCase(),
            resendVerificationCode = FakeResendVerificationCodeUseCase(),
            restartSignUp = fakeRestartSignUp,
            email = "user@test.com",
        )

        // When
        viewModel.onRestartSignUp()
        advanceUntilIdle()

        // Then
        assertEquals(1, fakeRestartSignUp.invokeCount)
        assertEquals(ValidateCodeUiState.RestartSignUp, viewModel.state.value)
    }
}
