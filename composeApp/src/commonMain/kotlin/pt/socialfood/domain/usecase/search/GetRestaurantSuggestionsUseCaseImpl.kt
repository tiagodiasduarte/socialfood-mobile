package pt.socialfood.domain.usecase.search

import pt.socialfood.core.Result
import pt.socialfood.domain.model.RestaurantSuggestions
import pt.socialfood.domain.repository.SearchRepository

class GetRestaurantSuggestionsUseCaseImpl(private val repository: SearchRepository) : GetRestaurantSuggestionsUseCase {
    override suspend fun invoke(): Result<RestaurantSuggestions> = repository.getRestaurantSuggestions()
}
