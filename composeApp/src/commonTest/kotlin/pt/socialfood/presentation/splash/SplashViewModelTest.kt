package pt.socialfood.presentation.splash

import com.russhwolf.settings.MapSettings
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
import pt.socialfood.domain.model.Configs
import pt.socialfood.domain.model.User
import pt.socialfood.fakes.FakeGetConfigsUseCase
import pt.socialfood.fakes.FakeGetUserMeUseCase
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given pendingVerificationEmail set and no token when loaded then emits NavigateToValidateToken without calling getUserMe or getConfigs`() = runTest {
        // Given
        val sessionManager = SessionManager(MapSettings())
        sessionManager.savePendingVerification("pending@test.com")
        val fakeGetUserMe = FakeGetUserMeUseCase(Result.Success(defaultUser()))
        val fakeGetConfigs = FakeGetConfigsUseCase(Result.Success(Configs(version = "1.0.0")))

        // When
        val viewModel = SplashViewModel(fakeGetUserMe, fakeGetConfigs, sessionManager)
        advanceUntilIdle()

        // Then
        assertEquals(SplashUiState.NavigateToValidateToken("pending@test.com"), viewModel.state.value)
        assertEquals(0, fakeGetUserMe.invokeCount)
        assertEquals(0, fakeGetConfigs.invokeCount)
    }

    @Test
    fun `given no pendingVerificationEmail and no token when loaded then emits NavigateToLogin`() = runTest {
        // Given
        val sessionManager = SessionManager(MapSettings())
        val fakeGetUserMe = FakeGetUserMeUseCase(Result.Success(defaultUser()))
        val fakeGetConfigs = FakeGetConfigsUseCase(Result.Success(Configs(version = "1.0.0")))

        // When
        val viewModel = SplashViewModel(fakeGetUserMe, fakeGetConfigs, sessionManager)
        advanceUntilIdle()

        // Then
        assertEquals(SplashUiState.NavigateToLogin, viewModel.state.value)
    }

    @Test
    fun `given no pendingVerificationEmail and token and unverified user when loaded then emits NavigateToValidateToken`() = runTest {
        // Given
        val sessionManager = SessionManager(MapSettings())
        sessionManager.saveToken("jwt-token")
        val fakeGetUserMe = FakeGetUserMeUseCase(Result.Success(defaultUser(isVerified = false)))
        val fakeGetConfigs = FakeGetConfigsUseCase(Result.Success(Configs(version = "1.0.0")))

        // When
        val viewModel = SplashViewModel(fakeGetUserMe, fakeGetConfigs, sessionManager)
        advanceUntilIdle()

        // Then
        assertEquals(SplashUiState.NavigateToValidateToken(defaultUser().email), viewModel.state.value)
    }

    @Test
    fun `given no pendingVerificationEmail and token and verified user when loaded then emits NavigateToHome`() = runTest {
        // Given
        val sessionManager = SessionManager(MapSettings())
        sessionManager.saveToken("jwt-token")
        val fakeGetUserMe = FakeGetUserMeUseCase(Result.Success(defaultUser(isVerified = true)))
        val fakeGetConfigs = FakeGetConfigsUseCase(Result.Success(Configs(version = "1.0.0")))

        // When
        val viewModel = SplashViewModel(fakeGetUserMe, fakeGetConfigs, sessionManager)
        advanceUntilIdle()

        // Then
        assertEquals(SplashUiState.NavigateToHome, viewModel.state.value)
    }

    @Test
    fun `given no pendingVerificationEmail and token and getUserMe fails when loaded then emits NavigateToLogin`() = runTest {
        // Given
        val sessionManager = SessionManager(MapSettings())
        sessionManager.saveToken("jwt-token")
        val fakeGetUserMe = FakeGetUserMeUseCase(Result.Error(ErrorEntity.Unknown))
        val fakeGetConfigs = FakeGetConfigsUseCase(Result.Success(Configs(version = "1.0.0")))

        // When
        val viewModel = SplashViewModel(fakeGetUserMe, fakeGetConfigs, sessionManager)
        advanceUntilIdle()

        // Then
        assertIs<SplashUiState.NavigateToLogin>(viewModel.state.value)
    }

    private fun defaultUser(isVerified: Boolean = true) = User(
        id = "1",
        email = "john.doe@test.com",
        name = "John Doe",
        isVerified = isVerified,
    )
}
