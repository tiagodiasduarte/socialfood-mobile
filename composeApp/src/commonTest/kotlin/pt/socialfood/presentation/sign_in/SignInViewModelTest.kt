package pt.socialfood.presentation.sign_in

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.core.Result
import pt.socialfood.data.network.SessionManager
import pt.socialfood.domain.error.ErrorEntity
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
    fun `given a new view model when created then state is Idle`() {
        // Given
        val vm = createViewModel(Result.Success("token"))

        // When / Then
        assertEquals(SignInUiState.Idle, vm.state.value)
    }

    @Test
    fun `given an empty email when sign in is called then state is InvalidCredentials error`() = runTestWithMainDispatcher {
        // Given
        val vm = createViewModel(Result.Success("token"))

        // When
        vm.onSignIn("", "password")
        advanceUntilIdle()

        // Then
        assertIs<SignInUiState.Error>(vm.state.value)
        assertEquals(ErrorEntity.InvalidCredentials, (vm.state.value as SignInUiState.Error).error)
    }

    @Test
    fun `given an empty password when sign in is called then state is InvalidCredentials error`() = runTestWithMainDispatcher {
        // Given
        val vm = createViewModel(Result.Success("token"))

        // When
        vm.onSignIn("user@test.com", "")
        advanceUntilIdle()

        // Then
        assertIs<SignInUiState.Error>(vm.state.value)
        assertEquals(ErrorEntity.InvalidCredentials, (vm.state.value as SignInUiState.Error).error)
    }

    @Test
    fun `given valid credentials when sign in is called then state is Success`() = runTestWithMainDispatcher {
        // Given
        val vm = createViewModel(Result.Success("token"))

        // When
        vm.onSignIn("user@test.com", "password")
        advanceUntilIdle()

        // Then
        assertEquals(SignInUiState.Success, vm.state.value)
    }

    @Test
    fun `given a failing sign in when sign in is called then state is Unknown error`() = runTestWithMainDispatcher {
        // Given
        val vm = createViewModel(Result.Error(ErrorEntity.Unknown))

        // When
        vm.onSignIn("user@test.com", "password")
        advanceUntilIdle()

        // Then
        assertIs<SignInUiState.Error>(vm.state.value)
        assertEquals(ErrorEntity.Unknown, (vm.state.value as SignInUiState.Error).error)
    }
}
