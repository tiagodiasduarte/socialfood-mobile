package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.domain.usecase.restaurantvisitstatus.GetVisitStatusUseCase

class FakeGetVisitStatusUseCase(private val result: Result<VisitStatus?> = Result.Success(null)) :
    GetVisitStatusUseCase {
    override suspend fun invoke(restaurantId: String): Result<VisitStatus?> = result
}
