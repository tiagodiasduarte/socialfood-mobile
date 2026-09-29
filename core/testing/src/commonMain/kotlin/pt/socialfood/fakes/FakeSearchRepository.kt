package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.GuideSuggestions
import pt.socialfood.domain.model.RestaurantSuggestions
import pt.socialfood.domain.model.Search
import pt.socialfood.domain.repository.SearchRepository

class FakeSearchRepository(
    private val result: Result<List<Search>> = Result.Success(emptyList()),
    private val restaurantSuggestionsResult: Result<RestaurantSuggestions> = Result.Success(
        RestaurantSuggestions(restaurants = emptyList(), generatedAt = ""),
    ),
    private val guideSuggestionsResult: Result<GuideSuggestions> = Result.Success(
        GuideSuggestions(guides = emptyList(), generatedAt = ""),
    ),
) : SearchRepository {
    var searchInvokeCount: Int = 0
        private set
    var lastPage: Int? = null
        private set
    var lastLimit: Int? = null
        private set
    var lastQuery: String? = null
        private set

    var restaurantSuggestionsInvokeCount: Int = 0
        private set

    var guideSuggestionsInvokeCount: Int = 0
        private set

    override suspend fun search(page: Int, limit: Int, query: String?): Result<List<Search>> {
        searchInvokeCount++
        lastPage = page
        lastLimit = limit
        lastQuery = query
        return result
    }

    override suspend fun getRestaurantSuggestions(): Result<RestaurantSuggestions> {
        restaurantSuggestionsInvokeCount++
        return restaurantSuggestionsResult
    }

    override suspend fun getGuideSuggestions(): Result<GuideSuggestions> {
        guideSuggestionsInvokeCount++
        return guideSuggestionsResult
    }
}
