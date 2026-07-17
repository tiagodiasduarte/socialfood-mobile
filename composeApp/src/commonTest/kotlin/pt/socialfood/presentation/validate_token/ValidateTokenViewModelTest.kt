package pt.socialfood.presentation.validate_token

import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import pt.socialfood.data.network.SessionManager
import pt.socialfood.domain.use_case.login.RestartSignUpUseCaseImpl
import pt.socialfood.fakes.FakeResendVerificationUseCase
import pt.socialfood.fakes.FakeValidateTokenUseCase
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class ValidateTokenViewModelTest {

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
    fun `given a pending verification email persisted when onRestartSignUp is called then pendingVerificationEmail is cleared and state becomes RestartSignUp`() = runTest {
        // Given
        val sessionManager = SessionManager(MapSettings())
        sessionManager.savePendingVerification("user@test.com")
        val viewModel = ValidateTokenViewModel(
            validateToken = FakeValidateTokenUseCase(),
            resendVerification = FakeResendVerificationUseCase(),
            restartSignUp = RestartSignUpUseCaseImpl(sessionManager),
            email = "user@test.com",
        )

        // When
        viewModel.onRestartSignUp()
        advanceUntilIdle()

        // Then
        assertNull(sessionManager.pendingVerificationEmail)
        assertEquals(ValidateTokenUiState.RestartSignUp, viewModel.state.value)
    }
}
