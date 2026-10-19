package pt.socialfood.core.domain.usecase.login

import pt.socialfood.core.common.Result

interface RegisterUseCase {
    suspend operator fun invoke(name: String, email: String, password: String): Result<Boolean>
}
