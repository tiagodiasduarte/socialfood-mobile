package pt.socialfood.domain.repository

import pt.socialfood.core.Result

interface AuthRepository {

    suspend fun login(email: String, password: String): Result<String>
    suspend fun register(name: String, email: String, password: String): Result<Boolean>
    suspend fun validateToken(token: String): Result<String>
    suspend fun resendVerification(email: String): Result<Boolean>
    suspend fun loginWithGoogle(idToken: String): Result<String>
    suspend fun logout(): Result<Boolean>

}