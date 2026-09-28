package pt.socialfood.domain.usecase.login

import pt.socialfood.core.Result

interface LoginUseCase {
    suspend operator fun invoke(email: String, password: String): Result<Boolean>
}
