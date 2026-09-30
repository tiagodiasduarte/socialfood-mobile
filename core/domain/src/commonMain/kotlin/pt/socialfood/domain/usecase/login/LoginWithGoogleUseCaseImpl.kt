package pt.socialfood.domain.usecase.login

import pt.socialfood.core.Result
import pt.socialfood.domain.repository.AuthRepository
import pt.socialfood.domain.session.SessionManager

internal class LoginWithGoogleUseCaseImpl(
    private val sessionManager: SessionManager,
    private val repository: AuthRepository,
) : LoginWithGoogleUseCase {
    override suspend operator fun invoke(idToken: String): Result<Boolean> =
        when (val result = repository.loginWithGoogle(idToken)) {
            is Result.Success -> {
                sessionManager.saveTokens(result.data.accessToken, result.data.refreshToken)
                Result.Success(true)
            }
            is Result.Failure -> Result.Failure(result.error)
        }
}
