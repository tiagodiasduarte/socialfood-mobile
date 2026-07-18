package pt.socialfood.presentation.splash

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.core.Result
import pt.socialfood.domain.error.ErrorEntity
import pt.socialfood.domain.model.Configs
import pt.socialfood.domain.model.User
import pt.socialfood.fakes.FakeGetConfigsUseCase
import pt.socialfood.fakes.FakeGetUserMeUseCase
import pt.socialfood.fakes.FakeSettingsRepository
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    @Test
    fun `given pendingVerificationEmail set and no token when loaded then emits NavigateToValidateCode without calling getUserMe or getConfigs`() =
        runTestWithMainDispatcher {
            // Given
            val settingsRepository = FakeSettingsRepository()
            settingsRepository.savePendingVerificationEmail("pending@test.com")
            val fakeGetUserMe = FakeGetUserMeUseCase(Result.Success(defaultUser()))
            val fakeGetConfigs = FakeGetConfigsUseCase(Result.Success(Configs(version = "1.0.0")))

            // When
            val viewModel = SplashViewModel(fakeGetUserMe, fakeGetConfigs, settingsRepository)
            advanceUntilIdle()

            // Then
            assertEquals(
                SplashUiState.NavigateToValidateCode("pending@test.com"),
                viewModel.state.value
            )
            assertEquals(0, fakeGetUserMe.invokeCount)
            assertEquals(0, fakeGetConfigs.invokeCount)
        }

    @Test
    fun `given no pendingVerificationEmail and no token when loaded then emits NavigateToLogin`() =
        runTestWithMainDispatcher {
            // Given
            val settingsRepository = FakeSettingsRepository()
            val fakeGetUserMe = FakeGetUserMeUseCase(Result.Success(defaultUser()))
            val fakeGetConfigs = FakeGetConfigsUseCase(Result.Success(Configs(version = "1.0.0")))

            // When
            val viewModel = SplashViewModel(fakeGetUserMe, fakeGetConfigs, settingsRepository)
            advanceUntilIdle()

            // Then
            assertEquals(SplashUiState.NavigateToLogin, viewModel.state.value)
        }

    @Test
    fun `given no pendingVerificationEmail and token and unverified user when loaded then emits NavigateToValidateCode`() =
        runTestWithMainDispatcher {
            // Given
            val settingsRepository = FakeSettingsRepository()
            settingsRepository.saveToken("jwt-token")
            val fakeGetUserMe =
                FakeGetUserMeUseCase(Result.Success(defaultUser(isVerified = false)))
            val fakeGetConfigs = FakeGetConfigsUseCase(Result.Success(Configs(version = "1.0.0")))

            // When
            val viewModel = SplashViewModel(fakeGetUserMe, fakeGetConfigs, settingsRepository)
            advanceUntilIdle()

            // Then
            assertEquals(
                SplashUiState.NavigateToValidateCode(defaultUser().email),
                viewModel.state.value
            )
        }

    @Test
    fun `given no pendingVerificationEmail and token and verified user when loaded then emits NavigateToHome`() =
        runTestWithMainDispatcher {
            // Given
            val settingsRepository = FakeSettingsRepository()
            settingsRepository.saveToken("jwt-token")
            val fakeGetUserMe = FakeGetUserMeUseCase(Result.Success(defaultUser(isVerified = true)))
            val fakeGetConfigs = FakeGetConfigsUseCase(Result.Success(Configs(version = "1.0.0")))

            // When
            val viewModel = SplashViewModel(fakeGetUserMe, fakeGetConfigs, settingsRepository)
            advanceUntilIdle()

            // Then
            assertEquals(SplashUiState.NavigateToHome, viewModel.state.value)
        }

    @Test
    fun `given stale pendingVerificationEmail and valid token for verified user when loaded then emits NavigateToHome ignoring stale pending flag`() =
        runTestWithMainDispatcher {
            // Given
            val settingsRepository = FakeSettingsRepository()
            settingsRepository.savePendingVerificationEmail("stale@test.com")
            settingsRepository.saveToken("jwt-token")
            val fakeGetUserMe = FakeGetUserMeUseCase(Result.Success(defaultUser(isVerified = true)))
            val fakeGetConfigs = FakeGetConfigsUseCase(Result.Success(Configs(version = "1.0.0")))

            // When
            val viewModel = SplashViewModel(fakeGetUserMe, fakeGetConfigs, settingsRepository)
            advanceUntilIdle()

            // Then
            assertEquals(SplashUiState.NavigateToHome, viewModel.state.value)
            assertEquals(1, fakeGetUserMe.invokeCount)
            assertEquals(1, fakeGetConfigs.invokeCount)
        }

    @Test
    fun `given no pendingVerificationEmail and token and getUserMe fails when loaded then emits NavigateToLogin`() =
        runTestWithMainDispatcher {
            // Given
            val settingsRepository = FakeSettingsRepository()
            settingsRepository.saveToken("jwt-token")
            val fakeGetUserMe = FakeGetUserMeUseCase(Result.Error(ErrorEntity.Unknown))
            val fakeGetConfigs = FakeGetConfigsUseCase(Result.Success(Configs(version = "1.0.0")))

            // When
            val viewModel = SplashViewModel(fakeGetUserMe, fakeGetConfigs, settingsRepository)
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
