package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.RestaurantSuggestions
import pt.socialfood.domain.model.Search
import pt.socialfood.domain.repository.SearchRepository

class FakeSearchRepository(
    private val result: Result<List<Search>> = Result.Success(emptyList()),
    private val restaurantSuggestionsResult: Result<RestaurantSuggestions> = Result.Success(
        RestaurantSuggestions(restaurants = emptyList(), generatedAt = ""),
    ),
) : SearchRepository {
    var lastPage: Int? = null
        private set
    var lastLimit: Int? = null
        private set
    var lastQuery: String? = null
        private set

    override suspend fun search(page: Int, limit: Int, query: String?): Result<List<Search>> {
        lastPage = page
        lastLimit = limit
        lastQuery = query
        return result
    }

    override suspend fun getRestaurantSuggestions(): Result<RestaurantSuggestions> = restaurantSuggestionsResult
}
