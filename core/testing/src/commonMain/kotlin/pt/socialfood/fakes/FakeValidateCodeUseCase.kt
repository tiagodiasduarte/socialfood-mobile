package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.usecase.login.ValidateCodeUseCase

class FakeValidateCodeUseCase(private val result: Result<Boolean> = Result.Success(true)) : ValidateCodeUseCase {
    var invokeCount: Int = 0
        private set
    var lastEmail: String? = null
        private set
    var lastCode: String? = null
        private set

    override suspend fun invoke(email: String, code: String): Result<Boolean> {
        invokeCount++
        lastEmail = email
        lastCode = code
        return result
    }
}
