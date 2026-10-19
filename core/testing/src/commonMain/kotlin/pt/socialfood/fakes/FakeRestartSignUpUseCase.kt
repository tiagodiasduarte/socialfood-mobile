package pt.socialfood.fakes

import pt.socialfood.core.common.Result
import pt.socialfood.core.domain.usecase.login.RestartSignUpUseCase

class FakeRestartSignUpUseCase(private val result: Result<Boolean> = Result.Success(true)) : RestartSignUpUseCase {
    var invokeCount = 0
        private set

    override suspend fun invoke(): Result<Boolean> {
        invokeCount++
        return result
    }
}
