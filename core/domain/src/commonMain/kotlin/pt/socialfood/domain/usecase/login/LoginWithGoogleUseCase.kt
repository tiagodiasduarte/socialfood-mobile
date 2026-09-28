package pt.socialfood.domain.usecase.login

import pt.socialfood.core.Result

interface LoginWithGoogleUseCase {
    suspend operator fun invoke(idToken: String): Result<Boolean>
}
