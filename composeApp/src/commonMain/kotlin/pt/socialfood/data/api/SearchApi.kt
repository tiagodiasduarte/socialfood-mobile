package pt.socialfood.data.api

import pt.socialfood.data.network.model.search.SearchResponse

interface SearchApi {
    suspend fun search(page: Int, limit: Int, query: String? = null): SearchResponse
}
