package pt.socialfood.domain.use_case.login

import pt.socialfood.core.Result
import pt.socialfood.data.network.SessionManager
import pt.socialfood.domain.error.ErrorEntity
import pt.socialfood.domain.repository.AuthRepository

class LoginUseCaseImpl(
    private val sessionManager: SessionManager,
    private val repository: AuthRepository,
) : LoginUseCase {
    override suspend operator fun invoke(email: String, password: String): Result<Boolean> {
        val result = repository.login(email, password)

        if (result is Result.Success) {
            sessionManager.saveToken(result.data)
            return Result.Success(true)
        }

        return Result.Error(ErrorEntity.Unknown)
    }
}
