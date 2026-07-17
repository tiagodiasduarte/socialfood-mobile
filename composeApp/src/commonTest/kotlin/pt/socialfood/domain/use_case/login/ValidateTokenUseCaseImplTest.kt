package pt.socialfood.domain.use_case.login

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.data.network.SessionManager
import pt.socialfood.domain.error.ErrorEntity
import pt.socialfood.fakes.FakeAuthRepository
import pt.socialfood.fakes.FakeSettingsRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class ValidateTokenUseCaseImplTest {

    @Test
    fun `given successful validate when invoked then token is saved and pendingVerificationEmail is cleared`() = runTest {
        // Given
        val settingsRepository = FakeSettingsRepository()
        settingsRepository.savePendingVerificationEmail("user@test.com")
        val sessionManager = SessionManager(settingsRepository)
        val fakeRepo = FakeAuthRepository(loginResult = Result.Success("jwt-token"))
        val useCase = ValidateTokenUseCaseImpl(sessionManager, fakeRepo, settingsRepository)

        // When
        val result = useCase.invoke("123456")

        // Then
        assertIs<Result.Success<Boolean>>(result)
        assertEquals("jwt-token", sessionManager.token)
        assertNull(settingsRepository.getPendingVerificationEmail())
    }

    @Test
    fun `given failed validate when invoked then pendingVerificationEmail is untouched and returns Error`() = runTest {
        // Given
        val settingsRepository = FakeSettingsRepository()
        settingsRepository.savePendingVerificationEmail("user@test.com")
        val sessionManager = SessionManager(settingsRepository)
        val fakeRepo = FakeAuthRepository(loginResult = Result.Error(ErrorEntity.Unknown))
        val useCase = ValidateTokenUseCaseImpl(sessionManager, fakeRepo, settingsRepository)

        // When
        val result = useCase.invoke("123456")

        // Then
        assertIs<Result.Error>(result)
        assertEquals("user@test.com", settingsRepository.getPendingVerificationEmail())
        assertNull(sessionManager.token)
    }
}
