package pt.socialfood.domain.use_case.login

import pt.socialfood.core.Result

interface RegisterUseCase {
    suspend operator fun invoke(name: String, email: String, password: String): Result<Boolean>
}
