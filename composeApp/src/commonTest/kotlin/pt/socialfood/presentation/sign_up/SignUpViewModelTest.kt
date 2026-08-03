package pt.socialfood.presentation.sign_up

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.fakes.FakeRegisterUseCase
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class SignUpViewModelTest {
    @Test
    fun `given an empty field when onSignUp is called then state is Error with a fill-in-fields message`() =
        runTestWithMainDispatcher {
            // Given
            val vm = SignUpViewModel(FakeRegisterUseCase())

            // When / Then
            vm.state.test {
                assertEquals(SignUpUiState.Idle, awaitItem())

                vm.onSignUp("", "user@test.com", "password", "password")

                val error = assertIs<SignUpUiState.Error>(awaitItem())
                assertEquals("Please fill in all fields", error.message)
            }
        }

    @Test
    fun `given mismatched passwords when onSignUp is called then state is Error with a mismatch message`() =
        runTestWithMainDispatcher {
            // Given
            val vm = SignUpViewModel(FakeRegisterUseCase())

            // When / Then
            vm.state.test {
                assertEquals(SignUpUiState.Idle, awaitItem())

                vm.onSignUp("Jane", "user@test.com", "password", "different")

                val error = assertIs<SignUpUiState.Error>(awaitItem())
                assertEquals("Passwords don't match", error.message)
            }
        }

    @Test
    fun `given the register use case fails when onSignUp is called then state is Error with the backend message`() =
        runTestWithMainDispatcher {
            // Given
            val register = FakeRegisterUseCase(Result.Failure(DataError.Network(Exception("test error"))))
            val vm = SignUpViewModel(register)

            // When / Then
            vm.state.test {
                assertEquals(SignUpUiState.Idle, awaitItem())

                vm.onSignUp("Jane", "user@test.com", "password", "password")

                assertEquals(SignUpUiState.Loading, awaitItem())
                val error = assertIs<SignUpUiState.Error>(awaitItem())
                assertEquals("Something went wrong", error.message)
            }
        }

    @Test
    fun `given valid credentials when onSignUp is called then state is Success`() = runTestWithMainDispatcher {
        // Given
        val vm = SignUpViewModel(FakeRegisterUseCase(Result.Success(true)))

        // When / Then
        vm.state.test {
            assertEquals(SignUpUiState.Idle, awaitItem())

            vm.onSignUp("Jane", "user@test.com", "password", "password")

            assertEquals(SignUpUiState.Loading, awaitItem())
            val success = assertIs<SignUpUiState.Success>(awaitItem())
            assertEquals("user@test.com", success.email)
        }
    }
}
