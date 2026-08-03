package pt.socialfood.data.repository

import pt.socialfood.core.Result
import pt.socialfood.data.api.AuthApi
import pt.socialfood.domain.error.safeApiCall
import pt.socialfood.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val authApi: AuthApi
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<String> =
        safeApiCall { authApi.login(email, password).token }

    override suspend fun register(name: String, email: String, password: String): Result<Unit> =
        safeApiCall { authApi.register(name, email, password) }

    override suspend fun validateCode(email: String, code: String): Result<String> =
        safeApiCall { authApi.validateCode(email = email, code = code).token }

    override suspend fun resendVerificationCode(email: String): Result<Unit> =
        safeApiCall { authApi.resendVerificationCode(email) }

    override suspend fun loginWithGoogle(idToken: String): Result<String> =
        safeApiCall { authApi.loginWithGoogle(idToken).token }

    override suspend fun logout(): Result<Boolean> =
        safeApiCall { authApi.logout() }
}
