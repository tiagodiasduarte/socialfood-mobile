package pt.socialfood.domain.usecase.restaurantvisitstatus

import pt.socialfood.core.Result
import pt.socialfood.domain.model.VisitStatus

interface GetVisitStatusUseCase {
    suspend operator fun invoke(restaurantId: String): Result<VisitStatus?>
}
