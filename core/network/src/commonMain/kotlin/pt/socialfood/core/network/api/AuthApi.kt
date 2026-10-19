package pt.socialfood.core.network.api

import pt.socialfood.core.network.model.login.LoginResponse
import pt.socialfood.core.network.model.login.ValidateCodeResponse

interface AuthApi {
    suspend fun login(username: String, password: String): LoginResponse
    suspend fun register(name: String, username: String, password: String)
    suspend fun loginWithGoogle(idToken: String): LoginResponse
    suspend fun validateCode(email: String, code: String): ValidateCodeResponse
    suspend fun resendVerificationCode(email: String)
    suspend fun logout(): Boolean
}
