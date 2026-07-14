package pt.socialfood.domain.use_case.login

import pt.socialfood.core.Result

interface ValidateTokenUseCase {
    suspend operator fun invoke(token: String): Result<Boolean>
}
