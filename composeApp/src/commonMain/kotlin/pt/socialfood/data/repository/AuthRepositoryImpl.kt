package pt.socialfood.data.repository

import io.ktor.client.plugins.ResponseException
import kotlinx.io.IOException
import pt.socialfood.core.Result
import pt.socialfood.data.api.AuthApi
import pt.socialfood.data.network.extensions.toErrorEntity
import pt.socialfood.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val authApi: AuthApi
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<String> {
        return try {

            val token = authApi.login(email,password).token
            Result.Success(token)

        } catch (e: IOException) {
            Result.Error(e.toErrorEntity())
        } catch (e: ResponseException) {
            Result.Error(e.toErrorEntity())
        }
    }

    override suspend fun register(name: String, email: String, password: String): Result<Unit> {
        return try {
            val token = authApi.register(name, email, password)
            Result.Success(token)
        } catch (e: IOException) {
            Result.Error(e.toErrorEntity())
        } catch (e: ResponseException) {
            Result.Error(e.toErrorEntity())
        }
    }

    override suspend fun validateCode(email: String, code: String): Result<String> {
        return try {
            val response = authApi.validateCode(email = email, code = code)
            Result.Success(response.token)
        } catch (e: IOException) {
            Result.Error(e.toErrorEntity())
        } catch (e: ResponseException) {
            Result.Error(e.toErrorEntity())
        }
    }

    override suspend fun resendVerificationCode(email: String): Result<Unit> {
        return try {
            val result = authApi.resendVerificationCode(email)
            Result.Success(result)
        } catch (e: IOException) {
            Result.Error(e.toErrorEntity())
        } catch (e: ResponseException) {
            Result.Error(e.toErrorEntity())
        }
    }

    override suspend fun loginWithGoogle(idToken: String): Result<String> {
        return try {
            val token = authApi.loginWithGoogle(idToken).token
            Result.Success(token)
        } catch (e: IOException) {
            Result.Error(e.toErrorEntity())
        } catch (e: ResponseException) {
            Result.Error(e.toErrorEntity())
        }
    }

    override suspend fun logout(): Result<Boolean> {
        return try {
            val result = authApi.logout()
            Result.Success(result)
        } catch (e: IOException) {
            Result.Error(e.toErrorEntity())
        } catch (e: ResponseException) {
            Result.Error(e.toErrorEntity())
        }
    }
}
