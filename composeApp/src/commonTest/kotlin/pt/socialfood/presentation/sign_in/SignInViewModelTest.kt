package pt.socialfood.presentation.sign_in

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import pt.socialfood.core.Result
import pt.socialfood.data.network.SessionManager
import pt.socialfood.domain.error.ErrorEntity
import pt.socialfood.domain.use_case.login.LoginUseCaseImpl
import pt.socialfood.domain.use_case.login.LoginWithGoogleUseCaseImpl
import pt.socialfood.fakes.FakeAuthRepository
import pt.socialfood.fakes.FakeSettingsRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class SignInViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(loginResult: Result<String>): SignInViewModel {
        val sessionManager = SessionManager(FakeSettingsRepository())
        val fakeRepo = FakeAuthRepository(loginResult)
        val loginUseCase = LoginUseCaseImpl(sessionManager, fakeRepo)
        val loginWithGoogleUseCase = LoginWithGoogleUseCaseImpl(sessionManager, fakeRepo)
        return SignInViewModel(loginUseCase, loginWithGoogleUseCase)
    }

    @Test
    fun initialStateIsIdle() {
        val vm = createViewModel(Result.Success("token"))
        assertEquals(SignInUiState.Idle, vm.state.value)
    }

    @Test
    fun emptyEmailEmitsInvalidCredentialsError() = runTest {
        val vm = createViewModel(Result.Success("token"))
        vm.onSignIn("", "password")
        advanceUntilIdle()
        assertIs<SignInUiState.Error>(vm.state.value)
        assertEquals(ErrorEntity.InvalidCredentials, (vm.state.value as SignInUiState.Error).error)
    }

    @Test
    fun emptyPasswordEmitsInvalidCredentialsError() = runTest {
        val vm = createViewModel(Result.Success("token"))
        vm.onSignIn("user@test.com", "")
        advanceUntilIdle()
        assertIs<SignInUiState.Error>(vm.state.value)
        assertEquals(ErrorEntity.InvalidCredentials, (vm.state.value as SignInUiState.Error).error)
    }

    @Test
    fun successfulSignInEmitsSuccess() = runTest {
        val vm = createViewModel(Result.Success("token"))
        vm.onSignIn("user@test.com", "password")
        advanceUntilIdle()
        assertEquals(SignInUiState.Success, vm.state.value)
    }

    @Test
    fun failedSignInEmitsUnknownError() = runTest {
        val vm = createViewModel(Result.Error(ErrorEntity.Unknown))
        vm.onSignIn("user@test.com", "password")
        advanceUntilIdle()
        assertIs<SignInUiState.Error>(vm.state.value)
        assertEquals(ErrorEntity.Unknown, (vm.state.value as SignInUiState.Error).error)
    }

    @Test
    fun loadingStateEmittedBeforeResult() = runTest {
        val vm = createViewModel(Result.Success("token"))
        vm.onSignIn("user@test.com", "password")
        advanceUntilIdle()
        assertEquals(SignInUiState.Success, vm.state.value)
    }
}
