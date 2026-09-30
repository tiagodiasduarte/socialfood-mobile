package pt.socialfood.presentation.signin

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.fakes.FakeLoginUseCase
import pt.socialfood.fakes.FakeLoginWithGoogleUseCase
import pt.socialfood.fakes.FakeUsersRepository
import pt.socialfood.feature.auth.generated.resources.Res
import pt.socialfood.feature.auth.generated.resources.sign_in_invalid_email
import pt.socialfood.feature.auth.generated.resources.sign_in_invalid_password
import pt.socialfood.random.nextUser
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SignInViewModelTest {
    private fun createViewModel(loginResult: Result<Boolean>): SignInViewModel {
        val usersRepository = FakeUsersRepository(getUserMeResult = Result.Success(Random.nextUser()))
        return SignInViewModel(FakeLoginUseCase(loginResult), FakeLoginWithGoogleUseCase(loginResult), usersRepository)
    }

    @Test
    fun `given a new view model when created then state is Idle`() = runTest {
        // Given
        val vm = createViewModel(Result.Success(true))

        // When / Then
        vm.state.test {
            assertEquals(SignInUiState.Idle, awaitItem())
        }
    }

    @Test
    fun `given an empty email when sign in is called then state is InvalidCredentials error`() =
        runTestWithMainDispatcher {
            // Given
            val vm = createViewModel(Result.Success(true))

            vm.state.test {
                assertEquals(SignInUiState.Idle, awaitItem())

                // When
                vm.onSignIn("", "password")

                // Then
                assertEquals(SignInUiState.ValidationError(Res.string.sign_in_invalid_email), awaitItem())
            }
        }

    @Test
    fun `given an empty password when sign in is called then state is InvalidCredentials error`() =
        runTestWithMainDispatcher {
            // Given
            val vm = createViewModel(Result.Success(true))

            vm.state.test {
                assertEquals(SignInUiState.Idle, awaitItem())

                // When
                vm.onSignIn("user@test.com", "")

                // Then
                assertEquals(SignInUiState.ValidationError(Res.string.sign_in_invalid_password), awaitItem())
            }
        }

    @Test
    fun `given valid credentials when sign in is called then state is Success`() = runTestWithMainDispatcher {
        // Given
        val vm = createViewModel(Result.Success(true))

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
    fun `given valid credentials when sign in is called then current user is refreshed`() = runTestWithMainDispatcher {
        // Given
        val loginUseCase = FakeLoginUseCase()
        val loginWithGoogleUseCase = FakeLoginWithGoogleUseCase()
        val usersRepository = FakeUsersRepository(getUserMeResult = Result.Success(Random.nextUser()))
        val vm = SignInViewModel(loginUseCase, loginWithGoogleUseCase, usersRepository)

        vm.state.test {
            assertEquals(SignInUiState.Idle, awaitItem())

            // When
            vm.onSignIn("user@test.com", "password")

            // Then
            assertEquals(SignInUiState.Loading, awaitItem())
            assertEquals(SignInUiState.Success, awaitItem())
            assertEquals(1, usersRepository.getUserMeInvokeCount)
        }
    }

    @Test
    fun `given sign in succeeds but current user fetch fails then state is Error`() = runTestWithMainDispatcher {
        // Given
        val loginUseCase = FakeLoginUseCase()
        val loginWithGoogleUseCase = FakeLoginWithGoogleUseCase()
        val usersRepository = FakeUsersRepository(
            getUserMeResult = Result.Failure(DataError.Network(Exception("test error"))),
        )
        val vm = SignInViewModel(loginUseCase, loginWithGoogleUseCase, usersRepository)

        vm.state.test {
            assertEquals(SignInUiState.Idle, awaitItem())

            // When
            vm.onSignIn("user@test.com", "password")

            // Then
            assertEquals(SignInUiState.Loading, awaitItem())
            assertEquals(SignInUiState.Error(ErrorCode.NETWORK), awaitItem())
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
            assertEquals(SignInUiState.Error(ErrorCode.NETWORK), awaitItem())
        }
    }

    @Test
    fun `given google sign in succeeds when onGoogleSignIn is called then state is Success`() =
        runTestWithMainDispatcher {
            // Given
            val vm = createViewModel(Result.Success(true))

            vm.state.test {
                assertEquals(SignInUiState.Idle, awaitItem())

                // When
                vm.onGoogleSignIn("id-token")

                // Then
                assertEquals(SignInUiState.Loading, awaitItem())
                assertEquals(SignInUiState.Success, awaitItem())
            }
        }

    @Test
    fun `given google sign in fails when onGoogleSignIn is called then state is Error`() = runTestWithMainDispatcher {
        // Given
        val vm = createViewModel(Result.Failure(DataError.Network(Exception("test error"))))

        vm.state.test {
            assertEquals(SignInUiState.Idle, awaitItem())

            // When
            vm.onGoogleSignIn("id-token")

            // Then
            assertEquals(SignInUiState.Loading, awaitItem())
            assertEquals(SignInUiState.Error(ErrorCode.NETWORK), awaitItem())
        }
    }

    @Test
    fun `given onGoogleSignInError is called then state is Error UNKNOWN with the debug message`() =
        runTestWithMainDispatcher {
            // Given
            val vm = createViewModel(Result.Success(true))

            vm.state.test {
                assertEquals(SignInUiState.Idle, awaitItem())

                // When
                vm.onGoogleSignInError("cancelled")

                // Then
                assertEquals(SignInUiState.Error(ErrorCode.UNKNOWN, debugMessage = "cancelled"), awaitItem())
            }
        }

    @Test
    fun `given an error state when resetState is called then state becomes Idle`() = runTestWithMainDispatcher {
        // Given
        val vm = createViewModel(Result.Failure(DataError.Network(Exception("test error"))))

        vm.state.test {
            assertEquals(SignInUiState.Idle, awaitItem())
            vm.onSignIn("user@test.com", "password")
            assertEquals(SignInUiState.Loading, awaitItem())
            assertEquals(SignInUiState.Error(ErrorCode.NETWORK), awaitItem())

            // When
            vm.resetState()

            // Then
            assertEquals(SignInUiState.Idle, awaitItem())
        }
    }
}
