package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.repository.AuthRepository

class FakeAuthRepository(
    private val loginResult: Result<String>,
    private val logoutResult: Result<Boolean> = Result.Success(true),
    private val registerResult: Result<Boolean> = Result.Success(true),
) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<String> = loginResult
    override suspend fun register(name: String, email: String, password: String): Result<Boolean> = registerResult
    override suspend fun validateToken(token: String): Result<String> = loginResult
    override suspend fun resendVerification(email: String): Result<Boolean> = Result.Success(true)
    override suspend fun loginWithGoogle(idToken: String): Result<String> = loginResult
    override suspend fun logout(): Result<Boolean> = logoutResult
}