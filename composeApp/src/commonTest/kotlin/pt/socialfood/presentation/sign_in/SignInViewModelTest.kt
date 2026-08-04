package pt.socialfood.presentation.sign_in

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.data.network.SessionManager
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.use_case.login.LoginUseCaseImpl
import pt.socialfood.domain.use_case.login.LoginWithGoogleUseCaseImpl
import pt.socialfood.fakes.FakeAuthRepository
import pt.socialfood.fakes.FakeSettingsRepository
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class SignInViewModelTest {
    private fun createViewModel(loginResult: Result<String>): SignInViewModel {
        val sessionManager = SessionManager(FakeSettingsRepository())
        val fakeRepo = FakeAuthRepository(loginResult)
        val loginUseCase = LoginUseCaseImpl(sessionManager, fakeRepo)
        val loginWithGoogleUseCase = LoginWithGoogleUseCaseImpl(sessionManager, fakeRepo)
        return SignInViewModel(loginUseCase, loginWithGoogleUseCase)
    }

    @Test
    fun `given a new view model when created then state is Idle`() =
        runTest {
            // Given
            val vm = createViewModel(Result.Success("token"))

            // When / Then
            vm.state.test {
                assertEquals(SignInUiState.Idle, awaitItem())
            }
        }

    @Test
    fun `given an empty email when sign in is called then state is InvalidCredentials error`() =
        runTestWithMainDispatcher {
            // Given
            val vm = createViewModel(Result.Success("token"))

            vm.state.test {
                assertEquals(SignInUiState.Idle, awaitItem())

                // When
                vm.onSignIn("", "password")

                // Then
                val state = awaitItem()
                assertIs<SignInUiState.Error>(state)
                assertEquals("Please enter your email", state.message)
            }
        }

    @Test
    fun `given an empty password when sign in is called then state is InvalidCredentials error`() =
        runTestWithMainDispatcher {
            // Given
            val vm = createViewModel(Result.Success("token"))

            vm.state.test {
                assertEquals(SignInUiState.Idle, awaitItem())

                // When
                vm.onSignIn("user@test.com", "")

                // Then
                val state = awaitItem()
                assertIs<SignInUiState.Error>(state)
                assertEquals("Please enter your password", state.message)
            }
        }

    @Test
    fun `given valid credentials when sign in is called then state is Success`() = runTestWithMainDispatcher {
        // Given
        val vm = createViewModel(Result.Success("token"))

        vm.state.test {
            assertEquals(SignInUiState.Idle, awaitItem())

            // When
            vm.onSignIn("user@test.com", "password")

            // Then
            assertEquals(SignInUiState.Loading, awaitItem())
            assertEquals(SignInUiState.Success, awaitItem())
        }
    }

    @Test
    fun `given a failing sign in when sign in is called then state is Unknown error`() = runTestWithMainDispatcher {
        // Given
        val vm = createViewModel(Result.Failure(DataError.Network(Exception("test error"))))

        vm.state.test {
            assertEquals(SignInUiState.Idle, awaitItem())

            // When
            vm.onSignIn("user@test.com", "password")

            // Then
            assertEquals(SignInUiState.Loading, awaitItem())
            val state = awaitItem()
            assertIs<SignInUiState.Error>(state)
            assertEquals("No internet connection. Please check your connection and try again.", state.message)
        }
    }
}
