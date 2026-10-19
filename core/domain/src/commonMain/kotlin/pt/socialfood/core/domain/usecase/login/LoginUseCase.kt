package pt.socialfood.core.domain.usecase.login

import pt.socialfood.core.common.Result

interface LoginUseCase {
    suspend operator fun invoke(email: String, password: String): Result<Boolean>
}
