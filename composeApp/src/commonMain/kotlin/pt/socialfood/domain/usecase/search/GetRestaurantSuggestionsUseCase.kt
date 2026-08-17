package pt.socialfood.domain.usecase.search

import pt.socialfood.core.Result
import pt.socialfood.domain.model.RestaurantSuggestions

interface GetRestaurantSuggestionsUseCase {
    suspend operator fun invoke(): Result<RestaurantSuggestions>
}
