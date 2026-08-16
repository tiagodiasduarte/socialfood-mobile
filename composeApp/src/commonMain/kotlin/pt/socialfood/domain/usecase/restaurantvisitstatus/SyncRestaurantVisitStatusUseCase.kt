package pt.socialfood.domain.usecase.restaurantvisitstatus

import pt.socialfood.core.Result
import pt.socialfood.domain.model.VisitStatus

interface SyncRestaurantVisitStatusUseCase {
    suspend operator fun invoke(status: VisitStatus): Result<Unit>
}
