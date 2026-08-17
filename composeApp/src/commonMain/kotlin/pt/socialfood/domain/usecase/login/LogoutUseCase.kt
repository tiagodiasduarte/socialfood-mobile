package pt.socialfood.domain.usecase.login

import pt.socialfood.core.Result

interface LogoutUseCase {
    suspend operator fun invoke(): Result<Boolean>
}
