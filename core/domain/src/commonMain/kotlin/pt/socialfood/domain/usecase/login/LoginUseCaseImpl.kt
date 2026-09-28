package pt.socialfood.domain.usecase.login

import pt.socialfood.core.Result
import pt.socialfood.domain.repository.AuthRepository
import pt.socialfood.domain.session.SessionManager

class LoginUseCaseImpl(private val sessionManager: SessionManager, private val repository: AuthRepository) :
    LoginUseCase {
    override suspend operator fun invoke(email: String, password: String): Result<Boolean> =
        when (val result = repository.login(email, password)) {
            is Result.Success -> {
                sessionManager.saveTokens(result.data.accessToken, result.data.refreshToken)
                Result.Success(true)
            }
            is Result.Failure -> Result.Failure(result.error)
        }
}
