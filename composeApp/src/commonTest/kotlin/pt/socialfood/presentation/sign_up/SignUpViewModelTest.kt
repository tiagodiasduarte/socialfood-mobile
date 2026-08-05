package pt.socialfood.presentation.sign_up

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.fakes.FakeRegisterUseCase
import pt.socialfood.random.nextEmail
import pt.socialfood.random.nextString
import pt.socialfood.runner.runTestWithMainDispatcher
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.sign_up_fill_all_fields
import socialfood.composeapp.generated.resources.sign_up_password_mismatch
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SignUpViewModelTest {
    @Test
    fun `given a required field is empty when onSignUp is called then state is ValidationError`() =
        runTestWithMainDispatcher {
            // Given
            val vm = SignUpViewModel(FakeRegisterUseCase())
            val password = Random.nextString()

            vm.state.test {
                assertEquals(SignUpUiState.Idle, awaitItem())

                // When
                vm.onSignUp(name = "", email = Random.nextEmail(), password = password, confirmPassword = password)

                // Then
                assertEquals(SignUpUiState.ValidationError(Res.string.sign_up_fill_all_fields), awaitItem())
            }
        }

    @Test
    fun `given passwords do not match when onSignUp is called then state is ValidationError`() =
        runTestWithMainDispatcher {
            // Given
            val vm = SignUpViewModel(FakeRegisterUseCase())

            vm.state.test {
                assertEquals(SignUpUiState.Idle, awaitItem())

                // When
                vm.onSignUp(
                    name = Random.nextString(),
                    email = Random.nextEmail(),
                    password = Random.nextString(),
                    confirmPassword = Random.nextString(),
                )

                // Then
                assertEquals(SignUpUiState.ValidationError(Res.string.sign_up_password_mismatch), awaitItem())
            }
        }

    @Test
    fun `given register succeeds when onSignUp is called then state is Success with email`() =
        runTestWithMainDispatcher {
            // Given
            val useCase = FakeRegisterUseCase(Result.Success(true))
            val vm = SignUpViewModel(useCase)
            val name = Random.nextString()
            val email = Random.nextEmail()
            val password = Random.nextString()

            vm.state.test {
                assertEquals(SignUpUiState.Idle, awaitItem())

                // When
                vm.onSignUp(name = name, email = email, password = password, confirmPassword = password)

                // Then
                assertEquals(SignUpUiState.Loading, awaitItem())
                assertEquals(SignUpUiState.Success(email), awaitItem())
            }
            assertEquals(name, useCase.lastName)
            assertEquals(password, useCase.lastPassword)
        }

    @Test
    fun `given register fails when onSignUp is called then state is Error`() = runTestWithMainDispatcher {
        // Given
        val useCase = FakeRegisterUseCase(Result.Failure(DataError.Network(Exception("test error"))))
        val vm = SignUpViewModel(useCase)
        val name = Random.nextString()
        val email = Random.nextEmail()
        val password = Random.nextString()

        vm.state.test {
            assertEquals(SignUpUiState.Idle, awaitItem())

            // When
            vm.onSignUp(name = name, email = email, password = password, confirmPassword = password)

            // Then
            assertEquals(SignUpUiState.Loading, awaitItem())
            assertEquals(SignUpUiState.Error(ErrorCode.NETWORK), awaitItem())
        }
    }
}
