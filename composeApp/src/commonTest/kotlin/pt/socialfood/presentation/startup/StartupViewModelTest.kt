package pt.socialfood.presentation.startup

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.model.Configs
import pt.socialfood.domain.model.User
import pt.socialfood.fakes.FakeGetConfigsUseCase
import pt.socialfood.fakes.FakeGetUserMeUseCase
import pt.socialfood.fakes.FakeSettingsRepository
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class StartupViewModelTest {
    @Test
    @Suppress("MaxLineLength")
    fun `given pendingVerificationEmail set and no token when loaded then emits NavigateToValidateCode without calling getUserMe or getConfigs`() =
        runTestWithMainDispatcher {
            // Given
            val settingsRepository = FakeSettingsRepository()
            settingsRepository.savePendingVerificationEmail("pending@test.com")
            val fakeGetUserMe = FakeGetUserMeUseCase(Result.Success(defaultUser()))
            val fakeGetConfigs = FakeGetConfigsUseCase(Result.Success(Configs(version = "1.0.0")))

            // When
            val viewModel = StartupViewModel(fakeGetUserMe, fakeGetConfigs, settingsRepository)

            // Then
            viewModel.state.test {
                assertEquals(StartupUiState.Loading, awaitItem())
                assertEquals(StartupUiState.NavigateToValidateCode("pending@test.com"), awaitItem())
                assertEquals(0, fakeGetUserMe.invokeCount)
                assertEquals(0, fakeGetConfigs.invokeCount)
            }
        }

    @Test
    fun `given no pendingVerificationEmail and no token when loaded then emits NavigateToLogin`() =
        runTestWithMainDispatcher {
            // Given
            val settingsRepository = FakeSettingsRepository()
            val fakeGetUserMe = FakeGetUserMeUseCase(Result.Success(defaultUser()))
            val fakeGetConfigs = FakeGetConfigsUseCase(Result.Success(Configs(version = "1.0.0")))

            // When
            val viewModel = StartupViewModel(fakeGetUserMe, fakeGetConfigs, settingsRepository)

            // Then
            viewModel.state.test {
                assertEquals(StartupUiState.Loading, awaitItem())
                assertEquals(StartupUiState.NavigateToLogin, awaitItem())
            }
        }

    @Test
    @Suppress("MaxLineLength")
    fun `given no pendingVerificationEmail and token and unverified user when loaded then emits NavigateToValidateCode`() =
        runTestWithMainDispatcher {
            // Given
            val settingsRepository = FakeSettingsRepository()
            settingsRepository.saveToken("jwt-token")
            val fakeGetUserMe =
                FakeGetUserMeUseCase(Result.Success(defaultUser(isVerified = false)))
            val fakeGetConfigs = FakeGetConfigsUseCase(Result.Success(Configs(version = "1.0.0")))

            // When
            val viewModel = StartupViewModel(fakeGetUserMe, fakeGetConfigs, settingsRepository)

            // Then
            viewModel.state.test {
                assertEquals(StartupUiState.Loading, awaitItem())
                assertEquals(
                    StartupUiState.NavigateToValidateCode(defaultUser().email),
                    awaitItem(),
                )
            }
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
            val viewModel = StartupViewModel(fakeGetUserMe, fakeGetConfigs, settingsRepository)

            // Then
            viewModel.state.test {
                assertEquals(StartupUiState.Loading, awaitItem())
                assertEquals(StartupUiState.NavigateToHome, awaitItem())
            }
        }

    @Test
    @Suppress("MaxLineLength")
    fun `given stale pendingVerificationEmail and valid token for verified user when loaded then emits NavigateToHome ignoring stale pending flag`() =
        runTestWithMainDispatcher {
            // Given
            val settingsRepository = FakeSettingsRepository()
            settingsRepository.savePendingVerificationEmail("stale@test.com")
            settingsRepository.saveToken("jwt-token")
            val fakeGetUserMe = FakeGetUserMeUseCase(Result.Success(defaultUser(isVerified = true)))
            val fakeGetConfigs = FakeGetConfigsUseCase(Result.Success(Configs(version = "1.0.0")))

            // When
            val viewModel = StartupViewModel(fakeGetUserMe, fakeGetConfigs, settingsRepository)

            // Then
            viewModel.state.test {
                assertEquals(StartupUiState.Loading, awaitItem())
                assertEquals(StartupUiState.NavigateToHome, awaitItem())
                assertEquals(1, fakeGetUserMe.invokeCount)
                assertEquals(1, fakeGetConfigs.invokeCount)
            }
        }

    @Test
    fun `given no pendingVerificationEmail and token and getUserMe fails when loaded then emits NavigateToLogin`() =
        runTestWithMainDispatcher {
            // Given
            val settingsRepository = FakeSettingsRepository()
            settingsRepository.saveToken("jwt-token")
            val fakeGetUserMe = FakeGetUserMeUseCase(Result.Failure(DataError.Network(Exception("test error"))))
            val fakeGetConfigs = FakeGetConfigsUseCase(Result.Success(Configs(version = "1.0.0")))

            // When
            val viewModel = StartupViewModel(fakeGetUserMe, fakeGetConfigs, settingsRepository)

            // Then
            viewModel.state.test {
                assertEquals(StartupUiState.Loading, awaitItem())
                assertEquals(StartupUiState.NavigateToLogin, awaitItem())
            }
        }

    private fun defaultUser(isVerified: Boolean = true) =
        User(
            id = "1",
            email = "john.doe@test.com",
            name = "John Doe",
            username = "johndoe",
            isVerified = isVerified,
        )
}
