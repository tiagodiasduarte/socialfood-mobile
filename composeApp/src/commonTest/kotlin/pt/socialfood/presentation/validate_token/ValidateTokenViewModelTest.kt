package pt.socialfood.presentation.validate_token

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import pt.socialfood.fakes.FakeResendVerificationUseCase
import pt.socialfood.fakes.FakeRestartSignUpUseCase
import pt.socialfood.fakes.FakeValidateTokenUseCase
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

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
    fun `given a fake restart sign up use case when onRestartSignUp is called then use case is invoked and state becomes RestartSignUp`() = runTest {
        // Given
        val fakeRestartSignUp = FakeRestartSignUpUseCase()
        val viewModel = ValidateTokenViewModel(
            validateToken = FakeValidateTokenUseCase(),
            resendVerification = FakeResendVerificationUseCase(),
            restartSignUp = fakeRestartSignUp,
            email = "user@test.com",
        )

        // When
        viewModel.onRestartSignUp()
        advanceUntilIdle()

        // Then
        assertEquals(1, fakeRestartSignUp.invokeCount)
        assertEquals(ValidateTokenUiState.RestartSignUp, viewModel.state.value)
    }
}
