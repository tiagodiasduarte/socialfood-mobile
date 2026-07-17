package pt.socialfood.domain.use_case.login

import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.data.network.SessionManager
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNull

class RestartSignUpUseCaseImplTest {

    @Test
    fun `given a pending verification email when invoked then pendingVerificationEmail is cleared and returns Success`() = runTest {
        // Given
        val settings = MapSettings()
        val sessionManager = SessionManager(settings)
        sessionManager.savePendingVerification("user@test.com")
        val useCase = RestartSignUpUseCaseImpl(sessionManager)

        // When
        val result = useCase.invoke()

        // Then
        assertIs<Result.Success<Boolean>>(result)
        assertNull(sessionManager.pendingVerificationEmail)
    }
}
