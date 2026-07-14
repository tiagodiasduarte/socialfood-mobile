package pt.socialfood.data

import pt.socialfood.data.network.model.LoginResponse
import pt.socialfood.data.network.model.login.ValidateTokenResponse

interface AuthApi {
    suspend fun login(username: String, password: String): LoginResponse
    suspend fun register(name: String, username: String, password: String): Boolean
    suspend fun loginWithGoogle(idToken: String): LoginResponse
    suspend fun validateToken(token: String): ValidateTokenResponse
    suspend fun resendVerification(email: String): Boolean
    suspend fun logout(): Boolean
}