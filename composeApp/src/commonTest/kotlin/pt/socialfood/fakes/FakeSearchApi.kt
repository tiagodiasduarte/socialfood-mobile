package pt.socialfood.fakes

import kotlinx.io.IOException
import pt.socialfood.data.api.SearchApi
import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.search.RestaurantSuggestionsResponse
import pt.socialfood.data.network.model.search.SearchResponse

class FakeSearchApi(
    private val response: SearchResponse = SearchResponse(
        authors = PagedResponse(items = emptyList(), page = 1, limit = 20, total = 0),
        guides = PagedResponse(items = emptyList(), page = 1, limit = 20, total = 0),
        restaurants = PagedResponse(items = emptyList(), page = 1, limit = 20, total = 0),
    ),
    private val restaurantSuggestionsResponse: RestaurantSuggestionsResponse = RestaurantSuggestionsResponse(
        restaurants = emptyList(),
        generatedAt = "",
    ),
    private val shouldThrow: Boolean = false,
) : SearchApi {
    var invokeCount: Int = 0
        private set
    var getRestaurantSuggestionsInvokeCount: Int = 0
        private set

    override suspend fun search(page: Int, limit: Int, query: String?): SearchResponse {
        invokeCount++
        if (shouldThrow) throw IOException("test error")
        return response
    }

    override suspend fun getRestaurantSuggestions(): RestaurantSuggestionsResponse {
        getRestaurantSuggestionsInvokeCount++
        if (shouldThrow) throw IOException("test error")
        return restaurantSuggestionsResponse
    }
}
