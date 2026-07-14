package pt.socialfood.domain.use_case.login

import pt.socialfood.core.Result
import pt.socialfood.domain.repository.AuthRepository

class ResendVerificationUseCaseImpl(
    private val repository: AuthRepository,
) : ResendVerificationUseCase {
    override suspend operator fun invoke(email: String): Result<Boolean> =
        repository.resendVerification(email)
}
