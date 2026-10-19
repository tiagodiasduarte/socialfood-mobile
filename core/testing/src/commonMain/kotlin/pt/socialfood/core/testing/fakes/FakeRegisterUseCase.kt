package pt.socialfood.core.testing.fakes

import pt.socialfood.core.common.Result
import pt.socialfood.core.domain.usecase.login.RegisterUseCase

class FakeRegisterUseCase(private val result: Result<Boolean> = Result.Success(true)) : RegisterUseCase {
    var invokeCount: Int = 0
        private set
    var lastName: String? = null
        private set
    var lastEmail: String? = null
        private set
    var lastPassword: String? = null
        private set

    override suspend fun invoke(name: String, email: String, password: String): Result<Boolean> {
        invokeCount++
        lastName = name
        lastEmail = email
        lastPassword = password
        return result
    }
}
