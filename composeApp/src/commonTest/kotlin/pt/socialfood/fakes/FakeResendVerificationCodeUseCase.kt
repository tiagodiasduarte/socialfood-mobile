package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.use_case.login.ResendVerificationCodeUseCase

class FakeResendVerificationCodeUseCase(
    private val result: Result<Unit> = Result.Success(Unit),
) : ResendVerificationCodeUseCase {
    override suspend fun invoke(email: String): Result<Unit> = result
}
