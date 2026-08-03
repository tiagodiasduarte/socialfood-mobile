package pt.socialfood.domain.use_case.login

import pt.socialfood.core.Result
import pt.socialfood.data.network.SessionManager
import pt.socialfood.domain.repository.AuthRepository

class LoginUseCaseImpl(
    private val sessionManager: SessionManager,
    private val repository: AuthRepository,
) : LoginUseCase {
    override suspend operator fun invoke(email: String, password: String): Result<Boolean> {
        return when (val result = repository.login(email, password)) {
            is Result.Success -> {
                sessionManager.saveToken(result.data)
                Result.Success(true)
            }
            is Result.Failure -> Result.Failure(result.error)
        }
    }
}
