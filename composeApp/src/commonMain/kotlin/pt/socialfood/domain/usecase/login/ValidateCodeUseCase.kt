package pt.socialfood.domain.usecase.login

import pt.socialfood.core.Result

interface ValidateCodeUseCase {
    suspend operator fun invoke(email: String, code: String): Result<Boolean>
}
