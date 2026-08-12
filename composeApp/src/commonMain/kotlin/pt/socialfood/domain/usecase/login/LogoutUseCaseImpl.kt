package pt.socialfood.domain.usecase.login

import pt.socialfood.core.Result
import pt.socialfood.data.network.SessionManager
import pt.socialfood.domain.repository.AuthRepository

class LogoutUseCaseImpl(
    private val sessionManager: SessionManager,
    private val repository: AuthRepository,
    ) : LogoutUseCase {
    override suspend operator fun invoke(): Result<Boolean> {
        val result = repository.logout()
        sessionManager.clear()   // always clear locally, even if the API call failed
        return result
    }
}
