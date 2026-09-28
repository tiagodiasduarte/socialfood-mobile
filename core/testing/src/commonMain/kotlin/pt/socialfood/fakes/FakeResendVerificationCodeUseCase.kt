package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.usecase.login.ResendVerificationCodeUseCase

class FakeResendVerificationCodeUseCase(private val result: Result<Unit> = Result.Success(Unit)) :
    ResendVerificationCodeUseCase {
    var invokeCount: Int = 0
        private set
    var lastEmail: String? = null
        private set

    override suspend fun invoke(email: String): Result<Unit> {
        invokeCount++
        lastEmail = email
        return result
    }
}
