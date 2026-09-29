package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.AuthTokens
import pt.socialfood.domain.repository.AuthRepository
import pt.socialfood.random.nextString
import kotlin.random.Random

class FakeAuthRepository(
    private val loginResult: Result<AuthTokens> = Result.Success(
        AuthTokens(accessToken = Random.nextString(), refreshToken = Random.nextString()),
    ),
    private val logoutResult: Result<Boolean> = Result.Success(true),
    private val registerResult: Result<Unit> = Result.Success(Unit),
    private val resendVerificationCodeResult: Result<Unit> = Result.Success(Unit),
) : AuthRepository {
    var resendVerificationCodeInvokeCount: Int = 0
        private set
    var lastResendVerificationCodeEmail: String? = null
        private set

    override suspend fun login(email: String, password: String): Result<AuthTokens> = loginResult
    override suspend fun register(name: String, email: String, password: String): Result<Unit> = registerResult
    override suspend fun validateCode(email: String, code: String): Result<AuthTokens> = loginResult
    override suspend fun resendVerificationCode(email: String): Result<Unit> {
        resendVerificationCodeInvokeCount++
        lastResendVerificationCodeEmail = email
        return resendVerificationCodeResult
    }
    override suspend fun loginWithGoogle(idToken: String): Result<AuthTokens> = loginResult
    override suspend fun logout(): Result<Boolean> = logoutResult
}
