package pt.socialfood.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import pt.socialfood.data.network.model.LoginRequest
import pt.socialfood.data.network.model.LoginResponse
import pt.socialfood.data.network.model.login.GoogleLoginRequest
import pt.socialfood.data.network.model.login.RegisterRequest
import pt.socialfood.data.network.model.login.ResendVerificationRequest
import pt.socialfood.data.network.model.login.ValidateTokenRequest
import pt.socialfood.data.network.model.login.ValidateTokenResponse

class AuthApiImpl(
    private val client: HttpClient
) : AuthApi {

    override suspend fun login(username: String, password: String): LoginResponse =
        client.post("auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(email = username, password = password))
        }.body()

    override suspend fun register(name: String, username: String, password: String): Boolean =
        client.post("auth/register") {
            contentType(ContentType.Application.Json)
            setBody(RegisterRequest(name = name, email = username, password = password))
        }.body()

    override suspend fun loginWithGoogle(idToken: String): LoginResponse =
        client.post("auth/google") {
            contentType(ContentType.Application.Json)
            setBody(GoogleLoginRequest(idToken = idToken))
        }.body()

    override suspend fun validateToken(token: String): ValidateTokenResponse =
        client.post("auth/verify") {
            contentType(ContentType.Application.Json)
            setBody(ValidateTokenRequest(token = token))
        }.body()

    override suspend fun resendVerification(email: String): Boolean =
        client.post("auth/send-verification") {
            contentType(ContentType.Application.Json)
            setBody(ResendVerificationRequest(email = email))
        }.body()

    override suspend fun logout(): Boolean =
        client.post("auth/logout") {
            contentType(ContentType.Application.Json)
        }.body()
}
