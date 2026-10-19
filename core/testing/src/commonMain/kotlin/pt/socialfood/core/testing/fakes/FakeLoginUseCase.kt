package pt.socialfood.core.testing.fakes

import pt.socialfood.core.common.Result
import pt.socialfood.core.domain.usecase.login.LoginUseCase

class FakeLoginUseCase(private val result: Result<Boolean> = Result.Success(true)) : LoginUseCase {
    var invokeCount: Int = 0
        private set

    override suspend operator fun invoke(email: String, password: String): Result<Boolean> {
        invokeCount++
        return result
    }
}
