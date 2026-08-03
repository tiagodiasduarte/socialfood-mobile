package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.use_case.login.RegisterUseCase

class FakeRegisterUseCase(private val result: Result<Boolean> = Result.Success(true)) : RegisterUseCase {
    var invokeCount: Int = 0
        private set

    override suspend fun invoke(name: String, email: String, password: String): Result<Boolean> {
        invokeCount++
        return result
    }
}
