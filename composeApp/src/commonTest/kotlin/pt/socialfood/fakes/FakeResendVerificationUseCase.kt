package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.use_case.login.ResendVerificationUseCase

class FakeResendVerificationUseCase(
    private val result: Result<Boolean> = Result.Success(true),
) : ResendVerificationUseCase {
    override suspend fun invoke(email: String): Result<Boolean> = result
}
