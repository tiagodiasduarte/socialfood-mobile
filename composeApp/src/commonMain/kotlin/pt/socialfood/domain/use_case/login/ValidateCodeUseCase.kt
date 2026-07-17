package pt.socialfood.domain.use_case.login

import pt.socialfood.core.Result

interface ValidateCodeUseCase {
    suspend operator fun invoke(email: String, token: String): Result<Boolean>
}
