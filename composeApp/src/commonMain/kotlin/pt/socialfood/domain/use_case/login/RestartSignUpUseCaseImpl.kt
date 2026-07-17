package pt.socialfood.domain.use_case.login

import pt.socialfood.core.Result
import pt.socialfood.data.network.SessionManager

class RestartSignUpUseCaseImpl(
    private val sessionManager: SessionManager,
) : RestartSignUpUseCase {
    override suspend operator fun invoke(): Result<Boolean> {
        sessionManager.clearPendingVerification()
        return Result.Success(true)
    }
}
