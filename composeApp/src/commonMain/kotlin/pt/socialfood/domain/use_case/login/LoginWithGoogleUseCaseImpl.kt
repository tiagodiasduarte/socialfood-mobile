package pt.socialfood.domain.use_case.login

import pt.socialfood.core.Result
import pt.socialfood.data.network.SessionManager
import pt.socialfood.domain.error.ErrorEntity
import pt.socialfood.domain.repository.AuthRepository

class LoginWithGoogleUseCaseImpl(
    private val sessionManager: SessionManager,
    private val repository: AuthRepository,
) : LoginWithGoogleUseCase {
    override suspend operator fun invoke(idToken: String): Result<Boolean> {
        val result = repository.loginWithGoogle(idToken)

        if (result is Result.Success) {
            sessionManager.saveToken(result.data)
            return Result.Success(true)
        }

        return Result.Error(ErrorEntity.Unknown)
    }
}
