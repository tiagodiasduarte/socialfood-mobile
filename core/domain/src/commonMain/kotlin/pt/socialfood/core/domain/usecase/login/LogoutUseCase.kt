package pt.socialfood.core.domain.usecase.login

import pt.socialfood.core.common.Result

interface LogoutUseCase {
    suspend operator fun invoke(): Result<Boolean>
}
