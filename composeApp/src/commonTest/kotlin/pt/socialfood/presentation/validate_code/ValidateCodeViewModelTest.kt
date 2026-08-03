package pt.socialfood.presentation.validate_code

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.fakes.FakeResendVerificationCodeUseCase
import pt.socialfood.fakes.FakeRestartSignUpUseCase
import pt.socialfood.fakes.FakeValidateCodeUseCase
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class ValidateCodeViewModelTest {
    @Test
    fun `given an empty code when onValidate is called then state is Error with an enter-the-code message`() =
        runTestWithMainDispatcher {
            // Given
            val viewModel =
                ValidateCodeViewModel(
                    validateCode = FakeValidateCodeUseCase(),
                    resendVerificationCode = FakeResendVerificationCodeUseCase(),
                    restartSignUp = FakeRestartSignUpUseCase(),
                    email = "user@test.com",
                )

            // When / Then
            viewModel.state.test {
                assertEquals(ValidateCodeUiState.Idle, awaitItem())

                viewModel.onValidate("")

                val error = assertIs<ValidateCodeUiState.Error>(awaitItem())
                assertEquals("Please enter the code", error.message)
            }
        }

    @Test
    @Suppress("MaxLineLength")
    fun `given the validateCode use case fails when onValidate is called then state is Error with the backend message`() =
        runTestWithMainDispatcher {
            // Given
            val validateCode = FakeValidateCodeUseCase(Result.Failure(DataError.Network(Exception("test error"))))
            val viewModel =
                ValidateCodeViewModel(
                    validateCode = validateCode,
                    resendVerificationCode = FakeResendVerificationCodeUseCase(),
                    restartSignUp = FakeRestartSignUpUseCase(),
                    email = "user@test.com",
                )

            // When / Then
            viewModel.state.test {
                assertEquals(ValidateCodeUiState.Idle, awaitItem())

                viewModel.onValidate("123456")

                assertEquals(ValidateCodeUiState.Loading, awaitItem())
                val error = assertIs<ValidateCodeUiState.Error>(awaitItem())
                assertEquals("Something went wrong", error.message)
            }
        }

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
