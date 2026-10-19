package pt.socialfood.core.domain.usecase.login

import pt.socialfood.core.common.Result

interface LoginWithGoogleUseCase {
    suspend operator fun invoke(idToken: String): Result<Boolean>
}
