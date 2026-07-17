package pt.socialfood.domain.use_case.login

import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.data.network.SessionManager
import pt.socialfood.domain.error.ErrorEntity
import pt.socialfood.fakes.FakeAuthRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class ValidateTokenUseCaseImplTest {

    @Test
    fun `given successful validate when invoked then token is saved and pendingVerificationEmail is cleared`() = runTest {
        // Given
        val settings = MapSettings()
        val sessionManager = SessionManager(settings)
        sessionManager.savePendingVerification("user@test.com")
        val fakeRepo = FakeAuthRepository(loginResult = Result.Success("jwt-token"))
        val useCase = ValidateTokenUseCaseImpl(sessionManager, fakeRepo)

        // When
        val result = useCase.invoke("123456")

        // Then
        assertIs<Result.Success<Boolean>>(result)
        assertEquals("jwt-token", sessionManager.token)
        assertNull(sessionManager.pendingVerificationEmail)
    }

    @Test
    fun `given failed validate when invoked then pendingVerificationEmail is untouched and returns Error`() = runTest {
        // Given
        val settings = MapSettings()
        val sessionManager = SessionManager(settings)
        sessionManager.savePendingVerification("user@test.com")
        val fakeRepo = FakeAuthRepository(loginResult = Result.Error(ErrorEntity.Unknown))
        val useCase = ValidateTokenUseCaseImpl(sessionManager, fakeRepo)

        // When
        val result = useCase.invoke("123456")

        // Then
        assertIs<Result.Error>(result)
        assertEquals("user@test.com", sessionManager.pendingVerificationEmail)
        assertNull(sessionManager.token)
    }
}
