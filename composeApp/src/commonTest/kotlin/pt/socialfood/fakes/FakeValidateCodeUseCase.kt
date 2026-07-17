package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.use_case.login.ValidateCodeUseCase

class FakeValidateCodeUseCase(
    private val result: Result<Boolean> = Result.Success(true),
) : ValidateCodeUseCase {
    override suspend fun invoke(email: String, code: String): Result<Boolean> = result
}
