package pt.socialfood.domain.use_case.login

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.data.network.SessionManager
import pt.socialfood.domain.error.ApiError
import pt.socialfood.fakes.FakeAuthRepository
import pt.socialfood.fakes.FakeSettingsRepository
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNull

class LogoutUseCaseImplTest {

    @Test
    fun `given valid session when logout is called then session is cleared and returns Success`() = runTest {
        // Given
        val sessionManager = SessionManager(FakeSettingsRepository())
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
        val sessionManager = SessionManager(FakeSettingsRepository())
        sessionManager.saveToken("some-jwt-token")
        val fakeRepo = FakeAuthRepository(
            loginResult = Result.Success("token"),
            logoutResult = Result.Failure(ApiError.Network(Exception("test error"))),
        )
        val useCase = LogoutUseCaseImpl(sessionManager, fakeRepo)

        // When
        val result = useCase.invoke()

        // Then
        assertIs<Result.Failure>(result)
        assertNull(sessionManager.token)
    }
}
