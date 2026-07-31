package pt.socialfood.data.repository

import kotlinx.io.IOException
import pt.socialfood.data.api.AuthApi
import pt.socialfood.data.network.model.login.LoginResponse
import pt.socialfood.data.network.model.login.ValidateCodeResponse

class FakeAuthApi(private val shouldThrow: Boolean = false) : AuthApi {

    override suspend fun login(username: String, password: String): LoginResponse {
        if (shouldThrow) throw IOException("test error")
        return LoginResponse(userId = "uid", token = "token")
    }

    override suspend fun register(name: String, username: String, password: String) {
        if (shouldThrow) throw IOException("test error")
    }

    override suspend fun loginWithGoogle(idToken: String): LoginResponse {
        if (shouldThrow) throw IOException("test error")
        return LoginResponse(userId = "uid", token = "token")
    }

    override suspend fun validateCode(email: String, code: String): ValidateCodeResponse {
        if (shouldThrow) throw IOException("test error")
        return ValidateCodeResponse(userId = "uid", token = "newtoken")
    }

    override suspend fun resendVerificationCode(email: String) {
        if (shouldThrow) throw IOException("test error")
    }

    override suspend fun logout(): Boolean {
        if (shouldThrow) throw IOException("test error")
        return true
    }
}
