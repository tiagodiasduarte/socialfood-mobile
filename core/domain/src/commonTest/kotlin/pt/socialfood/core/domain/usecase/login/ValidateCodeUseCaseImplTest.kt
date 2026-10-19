package pt.socialfood.core.domain.usecase.login

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.common.Result
import pt.socialfood.core.common.error.DataError
import pt.socialfood.core.domain.session.SessionManager
import pt.socialfood.core.model.AuthTokens
import pt.socialfood.fakes.FakeAuthRepository
import pt.socialfood.fakes.FakeSettingsRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class ValidateCodeUseCaseImplTest {
    @Test
    fun `given successful validate when invoked then tokens are saved and pendingVerificationEmail is cleared`() =
        runTest {
            // Given
            val settingsRepository = FakeSettingsRepository()
            settingsRepository.savePendingVerificationEmail("user@test.com")
            val sessionManager = SessionManager(settingsRepository)
            val fakeRepo = FakeAuthRepository(loginResult = Result.Success(AuthTokens("jwt-token", "refresh-token")))
            val useCase = ValidateCodeUseCaseImpl(sessionManager, fakeRepo, settingsRepository)

            // When
            val result = useCase.invoke("user@test.com", "123456")

            // Then
            assertIs<Result.Success<Boolean>>(result)
            assertEquals("jwt-token", sessionManager.accessToken)
            assertEquals("refresh-token", sessionManager.refreshToken)
            assertNull(settingsRepository.getPendingVerificationEmail())
        }

    @Test
    fun `given failed validate when invoked then pendingVerificationEmail is untouched and returns Error`() = runTest {
        // Given
        val settingsRepository = FakeSettingsRepository()
        settingsRepository.savePendingVerificationEmail("user@test.com")
        val sessionManager = SessionManager(settingsRepository)
        val fakeRepo = FakeAuthRepository(loginResult = Result.Failure(DataError.Network(Exception("test error"))))
        val useCase = ValidateCodeUseCaseImpl(sessionManager, fakeRepo, settingsRepository)

        // When
        val result = useCase.invoke("user@test.com", "123456")

        // Then
        assertIs<Result.Failure>(result)
        assertEquals("user@test.com", settingsRepository.getPendingVerificationEmail())
        assertNull(sessionManager.accessToken)
    }
}
