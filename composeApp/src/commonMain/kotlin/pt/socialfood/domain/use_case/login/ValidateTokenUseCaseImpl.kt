package pt.socialfood.domain.use_case.login

import pt.socialfood.core.Result
import pt.socialfood.data.network.SessionManager
import pt.socialfood.domain.error.ErrorEntity
import pt.socialfood.domain.repository.AuthRepository

class ValidateTokenUseCaseImpl(
    private val sessionManager: SessionManager,
    private val repository: AuthRepository,
) : ValidateTokenUseCase {
    override suspend operator fun invoke(token: String): Result<Boolean> {
        val result = repository.validateToken(token)

        if (result is Result.Success) {
            sessionManager.saveToken(result.data)
            sessionManager.clearPendingVerification()
            return Result.Success(true)
        }

        return Result.Error(ErrorEntity.Unknown)
    }
}
