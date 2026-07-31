package pt.socialfood.domain.use_case.login

import pt.socialfood.core.Result

interface LoginWithGoogleUseCase {
    suspend operator fun invoke(idToken: String): Result<Boolean>
}
