package pt.socialfood.domain.use_case.login

import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.data.network.SessionManager
import pt.socialfood.domain.error.ErrorEntity
import pt.socialfood.fakes.FakeAuthRepository
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNull

class LogoutUseCaseImplTest {

    @Test
    fun `given valid session when logout is called then session is cleared and returns Success`() = runTest {
        // Given
        val settings = MapSettings()
        val sessionManager = SessionManager(settings)
        sessionManager.saveToken("some-jwt-token")
        val fakeRepo = FakeAuthRepository(
            loginResult = Result.Success("token"),
            logoutResult = Result.Success(true),
        )
        val useCase = LogoutUseCaseImpl(sessionManager, fakeRepo)

        // When
        val result = useCase.invoke()

        // Then
        assertIs<Result.Success<Boolean>>(result)
        assertNull(sessionManager.token)
    }

    @Test
    fun `given api returns error when logout is called then session is still cleared and returns Error`() = runTest {
        // Given
        val settings = MapSettings()
        val sessionManager = SessionManager(settings)
        sessionManager.saveToken("some-jwt-token")
        val fakeRepo = FakeAuthRepository(
            loginResult = Result.Success("token"),
            logoutResult = Result.Error(ErrorEntity.Unknown),
        )
        val useCase = LogoutUseCaseImpl(sessionManager, fakeRepo)

        // When
        val result = useCase.invoke()

        // Then
        assertIs<Result.Error>(result)
        assertNull(sessionManager.token)
    }
}
