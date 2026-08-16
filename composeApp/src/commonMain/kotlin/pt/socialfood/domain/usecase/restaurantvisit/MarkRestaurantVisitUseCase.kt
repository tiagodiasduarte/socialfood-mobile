package pt.socialfood.domain.usecase.restaurantvisit

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.RestaurantVisitStatus

interface MarkRestaurantVisitUseCase {
    suspend operator fun invoke(restaurant: Restaurant, status: RestaurantVisitStatus): Result<Unit>
}
