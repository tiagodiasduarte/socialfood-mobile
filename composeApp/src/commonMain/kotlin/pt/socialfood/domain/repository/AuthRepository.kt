package pt.socialfood.domain.repository

import pt.socialfood.core.Result
import pt.socialfood.domain.model.AuthTokens

interface AuthRepository {

    suspend fun login(email: String, password: String): Result<AuthTokens>
    suspend fun register(name: String, email: String, password: String): Result<Unit>
    suspend fun validateCode(email: String, code: String): Result<AuthTokens>
    suspend fun resendVerificationCode(email: String): Result<Unit>
    suspend fun loginWithGoogle(idToken: String): Result<AuthTokens>
    suspend fun logout(): Result<Boolean>
}
