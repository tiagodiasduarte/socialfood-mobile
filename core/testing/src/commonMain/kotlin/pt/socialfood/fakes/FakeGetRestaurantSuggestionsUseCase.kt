package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.RestaurantSuggestions
import pt.socialfood.domain.usecase.search.GetRestaurantSuggestionsUseCase

class FakeGetRestaurantSuggestionsUseCase(
    private val result: Result<RestaurantSuggestions> = Result.Success(
        RestaurantSuggestions(restaurants = emptyList(), generatedAt = ""),
    ),
) : GetRestaurantSuggestionsUseCase {
    var invokeCount: Int = 0
        private set

    override suspend fun invoke(): Result<RestaurantSuggestions> {
        invokeCount++
        return result
    }
}
