package pt.socialfood.domain.use_case.login

import pt.socialfood.core.Result

interface LogoutUseCase {
    suspend operator fun invoke(): Result<Boolean>
}
