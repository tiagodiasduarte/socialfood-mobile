package pt.socialfood.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import pt.socialfood.data.network.model.search.GuideSuggestionsResponse
import pt.socialfood.data.network.model.search.RestaurantSuggestionsResponse
import pt.socialfood.data.network.model.search.SearchResponse

class SearchApiImpl(private val client: HttpClient) : SearchApi {

    override suspend fun search(page: Int, limit: Int, query: String?): SearchResponse = client.get("search") {
        parameter("page", page)
        parameter("limit", limit)
        if (!query.isNullOrBlank()) parameter("query", query)
    }.body()

    override suspend fun getRestaurantSuggestions(): RestaurantSuggestionsResponse =
        client.get("search/suggestions/restaurants").body()

    override suspend fun getGuideSuggestions(): GuideSuggestionsResponse =
        client.get("search/suggestions/guides").body()
}
