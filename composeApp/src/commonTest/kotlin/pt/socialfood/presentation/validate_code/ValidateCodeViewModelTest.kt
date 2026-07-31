package pt.socialfood.presentation.validate_code

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import pt.socialfood.fakes.FakeResendVerificationCodeUseCase
import pt.socialfood.fakes.FakeRestartSignUpUseCase
import pt.socialfood.fakes.FakeValidateCodeUseCase
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ValidateCodeViewModelTest {
    @Test
    @Suppress("MaxLineLength")
    fun `given a fake restart sign up use case when onRestartSignUp is called then use case is invoked and state becomes RestartSignUp`() =
        runTestWithMainDispatcher {
            // Given
            val fakeRestartSignUp = FakeRestartSignUpUseCase()
            val viewModel =
                ValidateCodeViewModel(
                    validateCode = FakeValidateCodeUseCase(),
                    resendVerificationCode = FakeResendVerificationCodeUseCase(),
                    restartSignUp = fakeRestartSignUp,
                    email = "user@test.com",
                )

            viewModel.state.test {
                assertEquals(ValidateCodeUiState.Idle, awaitItem())

                // When
                viewModel.onRestartSignUp()

                // Then
                assertEquals(ValidateCodeUiState.RestartSignUp, awaitItem())
                assertEquals(1, fakeRestartSignUp.invokeCount)
            }
        }
}
