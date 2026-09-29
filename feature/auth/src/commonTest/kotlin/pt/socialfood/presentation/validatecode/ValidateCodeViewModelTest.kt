package pt.socialfood.presentation.validatecode

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.fakes.FakeAuthRepository
import pt.socialfood.fakes.FakeRestartSignUpUseCase
import pt.socialfood.fakes.FakeValidateCodeUseCase
import pt.socialfood.feature.auth.generated.resources.Res
import pt.socialfood.feature.auth.generated.resources.validate_code_empty_code
import pt.socialfood.random.nextEmail
import pt.socialfood.random.nextString
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ValidateCodeViewModelTest {
    @Test
    fun `given an empty code when onValidate is called then state is ValidationError`() = runTestWithMainDispatcher {
        // Given
        val vm = ValidateCodeViewModel(
            validateCode = FakeValidateCodeUseCase(),
            authRepository = FakeAuthRepository(),
            restartSignUp = FakeRestartSignUpUseCase(),
            email = Random.nextEmail(),
        )

        vm.state.test {
            assertEquals(ValidateCodeUiState.Idle, awaitItem())

            // When
            vm.onValidate("")

            // Then
            assertEquals(ValidateCodeUiState.ValidationError(Res.string.validate_code_empty_code), awaitItem())
        }
    }

    @Test
    fun `given validateCode succeeds when onValidate is called then state is Success`() = runTestWithMainDispatcher {
        // Given
        val email = Random.nextEmail()
        val code = Random.nextString()
        val useCase = FakeValidateCodeUseCase(Result.Success(true))
        val vm = ValidateCodeViewModel(
            validateCode = useCase,
            authRepository = FakeAuthRepository(),
            restartSignUp = FakeRestartSignUpUseCase(),
            email = email,
        )

        vm.state.test {
            assertEquals(ValidateCodeUiState.Idle, awaitItem())

            // When
            vm.onValidate(code)

            // Then
            assertEquals(ValidateCodeUiState.Loading, awaitItem())
            assertEquals(ValidateCodeUiState.Success, awaitItem())
        }
        assertEquals(email, useCase.lastEmail)
        assertEquals(code, useCase.lastCode)
    }

    @Test
    fun `given validateCode fails when onValidate is called then state is Error`() = runTestWithMainDispatcher {
        // Given
        val useCase = FakeValidateCodeUseCase(Result.Failure(DataError.Network(Exception("test error"))))
        val vm = ValidateCodeViewModel(
            validateCode = useCase,
            authRepository = FakeAuthRepository(),
            restartSignUp = FakeRestartSignUpUseCase(),
            email = Random.nextEmail(),
        )

        vm.state.test {
            assertEquals(ValidateCodeUiState.Idle, awaitItem())

            // When
            vm.onValidate(Random.nextString())

            // Then
            assertEquals(ValidateCodeUiState.Loading, awaitItem())
            assertEquals(ValidateCodeUiState.Error(ErrorCode.NETWORK), awaitItem())
        }
    }

    @Test
    fun `given onResendCode is called then the verification code is resent to that email`() =
        runTestWithMainDispatcher {
            // Given
            val email = Random.nextEmail()
            val authRepository = FakeAuthRepository()
            val vm = ValidateCodeViewModel(
                validateCode = FakeValidateCodeUseCase(),
                authRepository = authRepository,
                restartSignUp = FakeRestartSignUpUseCase(),
                email = email,
            )

            // When
            vm.onResendCode()
            advanceUntilIdle()

            // Then
            assertEquals(1, authRepository.resendVerificationCodeInvokeCount)
            assertEquals(email, authRepository.lastResendVerificationCodeEmail)
        }

    @Test
    fun `given a fake restart sign up use case when onRestartSignUp is called then state becomes RestartSignUp`() =
        runTestWithMainDispatcher {
            // Given
            val fakeRestartSignUp = FakeRestartSignUpUseCase()
            val viewModel =
                ValidateCodeViewModel(
                    validateCode = FakeValidateCodeUseCase(),
                    authRepository = FakeAuthRepository(),
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
