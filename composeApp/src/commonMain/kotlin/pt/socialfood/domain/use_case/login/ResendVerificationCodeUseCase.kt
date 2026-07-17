package pt.socialfood.domain.use_case.login

import pt.socialfood.core.Result

interface ResendVerificationCodeUseCase {
    suspend operator fun invoke(email: String): Result<Unit>
}
