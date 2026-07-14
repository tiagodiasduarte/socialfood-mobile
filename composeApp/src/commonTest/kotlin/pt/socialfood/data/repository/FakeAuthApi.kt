package pt.socialfood.data.repository

import pt.socialfood.data.AuthApi
import pt.socialfood.data.network.model.LoginResponse
import pt.socialfood.data.network.model.login.ValidateTokenResponse

class FakeAuthApi(private val shouldThrow: Boolean = false) : AuthApi {

    override suspend fun login(username: String, password: String): LoginResponse {
        if (shouldThrow) throw RuntimeException("test error")
        return LoginResponse(userId = "uid", token = "token")
    }

    override suspend fun register(name: String, username: String, password: String): Boolean {
        if (shouldThrow) throw RuntimeException("test error")
        return true
    }

    override suspend fun loginWithGoogle(idToken: String): LoginResponse {
        if (shouldThrow) throw RuntimeException("test error")
        return LoginResponse(userId = "uid", token = "token")
    }

    override suspend fun validateToken(token: String): ValidateTokenResponse {
        if (shouldThrow) throw RuntimeException("test error")
        return ValidateTokenResponse(name = "name", email = "email@test.com", token = "newtoken")
    }

    override suspend fun resendVerification(email: String): Boolean {
        if (shouldThrow) throw RuntimeException("test error")
        return true
    }

    override suspend fun logout(): Boolean {
        if (shouldThrow) throw RuntimeException("test error")
        return true
    }
}
