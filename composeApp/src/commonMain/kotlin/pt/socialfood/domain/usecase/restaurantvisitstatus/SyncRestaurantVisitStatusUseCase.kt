package pt.socialfood.domain.usecase.restaurantvisitstatus

import pt.socialfood.core.Result

interface SyncRestaurantVisitStatusUseCase {
    suspend operator fun invoke(): Result<Unit>
}
