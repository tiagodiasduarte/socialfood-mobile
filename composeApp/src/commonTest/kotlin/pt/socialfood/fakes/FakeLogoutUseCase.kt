package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.use_case.login.LogoutUseCase

class FakeLogoutUseCase(private val result: Result<Boolean> = Result.Success(true)) : LogoutUseCase {
    var invokeCount: Int = 0
        private set

    override suspend fun invoke(): Result<Boolean> {
        invokeCount++
        return result
    }
}
