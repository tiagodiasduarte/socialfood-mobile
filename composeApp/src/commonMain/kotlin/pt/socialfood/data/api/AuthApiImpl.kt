package pt.socialfood.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import pt.socialfood.data.network.model.login.LoginRequest
import pt.socialfood.data.network.model.login.LoginResponse
import pt.socialfood.data.network.model.login.GoogleLoginRequest
import pt.socialfood.data.network.model.login.RegisterRequest
import pt.socialfood.data.network.model.login.ResendVerificationCodeRequest
import pt.socialfood.data.network.model.login.ValidateCodeRequest
import pt.socialfood.data.network.model.login.ValidateCodeResponse

class AuthApiImpl(
    private val client: HttpClient
) : AuthApi {

    override suspend fun login(username: String, password: String): LoginResponse =
        client.post("auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(email = username, password = password))
        }.body()

    override suspend fun register(name: String, username: String, password: String): Unit =
        client.post("auth/register") {
            contentType(ContentType.Application.Json)
            setBody(RegisterRequest(name = name, email = username, password = password))
        }.body()

    override suspend fun loginWithGoogle(idToken: String): LoginResponse =
        client.post("auth/google") {
            contentType(ContentType.Application.Json)
            setBody(GoogleLoginRequest(idToken = idToken))
        }.body()

    override suspend fun validateCode(email: String, code: String): ValidateCodeResponse =
        client.post("auth/verify") {
            contentType(ContentType.Application.Json)
            setBody(ValidateCodeRequest(email = email, code = code))
        }.body()

    override suspend fun resendVerificationCode(email: String): Unit =
        client.post("auth/send-verification") {
            contentType(ContentType.Application.Json)
            setBody(ResendVerificationCodeRequest(email = email))
        }.body()

    override suspend fun logout(): Boolean =
        client.post("auth/logout") {
            contentType(ContentType.Application.Json)
        }.body()
}
