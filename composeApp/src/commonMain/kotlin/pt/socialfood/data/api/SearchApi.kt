package pt.socialfood.data.api

import pt.socialfood.data.network.model.search.GuideSuggestionsResponse
import pt.socialfood.data.network.model.search.RestaurantSuggestionsResponse
import pt.socialfood.data.network.model.search.SearchResponse

interface SearchApi {
    suspend fun search(page: Int, limit: Int, query: String? = null): SearchResponse
    suspend fun getRestaurantSuggestions(): RestaurantSuggestionsResponse
    suspend fun getGuideSuggestions(): GuideSuggestionsResponse
}
