package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.usecase.login.LoginWithGoogleUseCase

class FakeLoginWithGoogleUseCase(private val result: Result<Boolean> = Result.Success(true)) : LoginWithGoogleUseCase {
    var invokeCount: Int = 0
        private set

    override suspend operator fun invoke(idToken: String): Result<Boolean> {
        invokeCount++
        return result
    }
}
