package pt.socialfood.domain.usecase.login

import pt.socialfood.core.Result
import pt.socialfood.domain.repository.AuthRepository

class ResendVerificationCodeUseCaseImpl(private val repository: AuthRepository) : ResendVerificationCodeUseCase {
    override suspend operator fun invoke(email: String): Result<Unit> = repository.resendVerificationCode(email)
}
