package pt.socialfood.domain.repository

import pt.socialfood.core.Result

interface AuthRepository {

    suspend fun login(email: String, password: String): Result<String>
    suspend fun register(name: String, email: String, password: String): Result<Unit>
    suspend fun validateCode(email: String, code: String): Result<String>
    suspend fun resendVerificationCode(email: String): Result<Unit>
    suspend fun loginWithGoogle(idToken: String): Result<String>
    suspend fun logout(): Result<Boolean>

}