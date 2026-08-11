package pt.socialfood.domain.usecase.login

import pt.socialfood.core.Result

interface ResendVerificationCodeUseCase {
    suspend operator fun invoke(email: String): Result<Unit>
}
