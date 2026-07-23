package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.use_case.favourite.IsGuideFavouriteUseCase

class FakeIsGuideFavouriteUseCase(
    private val result: Result<Boolean> = Result.Success(false),
) : IsGuideFavouriteUseCase {
    override suspend fun invoke(guideId: String): Result<Boolean> = result
}
