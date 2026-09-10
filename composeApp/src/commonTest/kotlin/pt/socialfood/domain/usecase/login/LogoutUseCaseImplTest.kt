package pt.socialfood.domain.usecase.login

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.data.network.SessionManager
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.model.AuthTokens
import pt.socialfood.fakes.FakeAuthRepository
import pt.socialfood.fakes.FakeLocalCacheRepository
import pt.socialfood.fakes.FakeSettingsRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class LogoutUseCaseImplTest {

    @Test
    fun `given valid session when logout is called then session and cache are cleared`() = runTest {
        // Given
        val sessionManager = SessionManager(FakeSettingsRepository())
        sessionManager.saveTokens("some-jwt-token", "some-refresh-token")
        val fakeRepo = FakeAuthRepository(
            loginResult = Result.Success(AuthTokens("token", "refresh-token")),
            logoutResult = Result.Success(true),
        )
        val localCacheRepository = FakeLocalCacheRepository()
        val useCase = LogoutUseCaseImpl(sessionManager, fakeRepo, localCacheRepository)

        // When
        val result = useCase.invoke()

        // Then
        assertIs<Result.Success<Boolean>>(result)
        assertNull(sessionManager.accessToken)
        assertEquals(1, localCacheRepository.clearAllCallCount)
    }

    @Test
    fun `given api returns error when logout is called then session is still cleared`() = runTest {
        // Given
        val sessionManager = SessionManager(FakeSettingsRepository())
        sessionManager.saveTokens("some-jwt-token", "some-refresh-token")
        val fakeRepo = FakeAuthRepository(
            loginResult = Result.Success(AuthTokens("token", "refresh-token")),
            logoutResult = Result.Failure(DataError.Network(Exception("test error"))),
        )
        val useCase = LogoutUseCaseImpl(sessionManager, fakeRepo, FakeLocalCacheRepository())

        // When
        val result = useCase.invoke()

        // Then
        assertIs<Result.Success<Boolean>>(result)
        assertNull(sessionManager.accessToken)
    }
}
