package pt.socialfood.domain.usecase.login

import pt.socialfood.core.Result

interface RestartSignUpUseCase {
    suspend operator fun invoke(): Result<Boolean>
}
