package pt.socialfood.core.network.api

import pt.socialfood.core.network.model.search.GuideSuggestionsResponse
import pt.socialfood.core.network.model.search.RestaurantSuggestionsResponse
import pt.socialfood.core.network.model.search.SearchResponse

interface SearchApi {
    suspend fun search(page: Int, limit: Int, query: String? = null): SearchResponse
    suspend fun getRestaurantSuggestions(): RestaurantSuggestionsResponse
    suspend fun getGuideSuggestions(): GuideSuggestionsResponse
}
