package pt.socialfood.data.repository

import pt.socialfood.core.common.Result
import pt.socialfood.core.domain.repository.AuthRepository
import pt.socialfood.core.model.AuthTokens
import pt.socialfood.data.api.AuthApi
import pt.socialfood.domain.error.safeApiCall

internal class AuthRepositoryImpl(private val authApi: AuthApi) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<AuthTokens> = safeApiCall {
        val response = authApi.login(email, password)
        AuthTokens(accessToken = response.token, refreshToken = response.refreshToken)
    }

    override suspend fun register(name: String, email: String, password: String): Result<Unit> =
        safeApiCall { authApi.register(name, email, password) }

    override suspend fun validateCode(email: String, code: String): Result<AuthTokens> = safeApiCall {
        val response = authApi.validateCode(email = email, code = code)
        AuthTokens(accessToken = response.token, refreshToken = response.refreshToken)
    }

    override suspend fun resendVerificationCode(email: String): Result<Unit> =
        safeApiCall { authApi.resendVerificationCode(email) }

    override suspend fun loginWithGoogle(idToken: String): Result<AuthTokens> = safeApiCall {
        val response = authApi.loginWithGoogle(idToken)
        AuthTokens(accessToken = response.token, refreshToken = response.refreshToken)
    }

    override suspend fun logout(): Result<Boolean> = safeApiCall { authApi.logout() }
}
