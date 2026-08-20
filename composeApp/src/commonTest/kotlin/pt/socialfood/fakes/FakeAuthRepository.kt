package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.AuthTokens
import pt.socialfood.domain.repository.AuthRepository

class FakeAuthRepository(
    private val loginResult: Result<AuthTokens>,
    private val logoutResult: Result<Boolean> = Result.Success(true),
    private val registerResult: Result<Unit> = Result.Success(Unit),
) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<AuthTokens> = loginResult
    override suspend fun register(name: String, email: String, password: String): Result<Unit> = registerResult
    override suspend fun validateCode(email: String, code: String): Result<AuthTokens> = loginResult
    override suspend fun resendVerificationCode(email: String): Result<Unit> = Result.Success(Unit)
    override suspend fun loginWithGoogle(idToken: String): Result<AuthTokens> = loginResult
    override suspend fun logout(): Result<Boolean> = logoutResult
}
