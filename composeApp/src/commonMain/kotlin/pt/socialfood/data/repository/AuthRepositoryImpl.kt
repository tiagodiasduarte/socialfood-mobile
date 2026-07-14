package pt.socialfood.data.repository

import pt.socialfood.core.Result
import pt.socialfood.data.AuthApi
import pt.socialfood.data.network.extensions.toErrorEntity
import pt.socialfood.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val authApi: AuthApi
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<String> {
        return try {

            val token = authApi.login(email,password).token
            Result.Success(token)

        } catch (exception: Exception) {
            Result.Error(exception.toErrorEntity())
        }
    }

    override suspend fun register(name: String, email: String, password: String): Result<Boolean> {
        return try {
            val token = authApi.register(name, email, password)
            Result.Success(token)
        } catch (exception: Exception) {
            Result.Error(exception.toErrorEntity())
        }
    }

    override suspend fun validateToken(token: String): Result<String> {
        return try {
            val response = authApi.validateToken(token)
            Result.Success(response.token)
        } catch (exception: Exception) {
            Result.Error(exception.toErrorEntity())
        }
    }

    override suspend fun resendVerification(email: String): Result<Boolean> {
        return try {
            val result = authApi.resendVerification(email)
            Result.Success(result)
        } catch (exception: Exception) {
            Result.Error(exception.toErrorEntity())
        }
    }

    override suspend fun loginWithGoogle(idToken: String): Result<String> {
        return try {
            val token = authApi.loginWithGoogle(idToken).token
            Result.Success(token)
        } catch (exception: Exception) {
            Result.Error(exception.toErrorEntity())
        }
    }

    override suspend fun logout(): Result<Boolean> {
        return try {
            val result = authApi.logout()
            Result.Success(result)
        } catch (exception: Exception) {
            Result.Error(exception.toErrorEntity())
        }
    }
}