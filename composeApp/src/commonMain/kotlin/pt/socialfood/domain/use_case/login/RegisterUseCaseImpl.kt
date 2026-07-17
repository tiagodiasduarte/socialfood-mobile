package pt.socialfood.domain.use_case.login

import pt.socialfood.core.Result
import pt.socialfood.data.network.SessionManager
import pt.socialfood.domain.error.ErrorEntity
import pt.socialfood.domain.repository.AuthRepository

class RegisterUseCaseImpl(
    private val repository: AuthRepository,
    private val sessionManager: SessionManager,
) : RegisterUseCase {
    override suspend operator fun invoke(
        name: String,
        email: String,
        password: String
    ): Result<Boolean> {
        val result = repository.register(name, email, password)

        if (result is Result.Success) {
            sessionManager.savePendingVerification(email)
            return Result.Success(true)
        }

        return Result.Error(ErrorEntity.Unknown)
    }
}
