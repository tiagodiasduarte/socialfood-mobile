package pt.socialfood.domain.usecase.restaurant

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Restaurant

interface GetRestaurantsUseCase {
    suspend operator fun invoke(): Result<List<Restaurant>>
}
