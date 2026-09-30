package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.usecase.login.LoginUseCase

class FakeLoginUseCase(private val result: Result<Boolean> = Result.Success(true)) : LoginUseCase {
    var invokeCount: Int = 0
        private set

    override suspend operator fun invoke(email: String, password: String): Result<Boolean> {
        invokeCount++
        return result
    }
}
