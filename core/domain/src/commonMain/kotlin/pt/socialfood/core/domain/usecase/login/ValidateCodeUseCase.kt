package pt.socialfood.core.domain.usecase.login

import pt.socialfood.core.common.Result

interface ValidateCodeUseCase {
    suspend operator fun invoke(email: String, code: String): Result<Boolean>
}
