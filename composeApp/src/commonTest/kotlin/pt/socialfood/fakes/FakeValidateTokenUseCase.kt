package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.use_case.login.ValidateTokenUseCase

class FakeValidateTokenUseCase(
    private val result: Result<Boolean> = Result.Success(true),
) : ValidateTokenUseCase {
    override suspend fun invoke(token: String): Result<Boolean> = result
}
