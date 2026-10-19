package pt.socialfood.core.domain.repository

import pt.socialfood.core.common.Result
import pt.socialfood.core.model.AuthTokens

interface AuthRepository {

    suspend fun login(email: String, password: String): Result<AuthTokens>
    suspend fun register(name: String, email: String, password: String): Result<Unit>
    suspend fun validateCode(email: String, code: String): Result<AuthTokens>
    suspend fun resendVerificationCode(email: String): Result<Unit>
    suspend fun loginWithGoogle(idToken: String): Result<AuthTokens>
    suspend fun logout(): Result<Boolean>
}
