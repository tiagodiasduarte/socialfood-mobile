package pt.socialfood.domain.use_case.login

import pt.socialfood.core.Result

interface LoginUseCase {
    suspend operator fun invoke(email: String, password: String): Result<Boolean>
}