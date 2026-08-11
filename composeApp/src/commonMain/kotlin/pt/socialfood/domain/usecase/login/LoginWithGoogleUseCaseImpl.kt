package pt.socialfood.domain.usecase.login

import pt.socialfood.core.Result
import pt.socialfood.data.network.SessionManager
import pt.socialfood.domain.repository.AuthRepository

class LoginWithGoogleUseCaseImpl(
    private val sessionManager: SessionManager,
    private val repository: AuthRepository,
) : LoginWithGoogleUseCase {
    override suspend operator fun invoke(idToken: String): Result<Boolean> {
        return when (val result = repository.loginWithGoogle(idToken)) {
            is Result.Success -> {
                sessionManager.saveToken(result.data)
                Result.Success(true)
            }
            is Result.Failure -> Result.Failure(result.error)
        }
    }
}
