package pt.socialfood.core.domain.repository

import pt.socialfood.core.common.Result
import pt.socialfood.core.model.GuideSuggestions
import pt.socialfood.core.model.RestaurantSuggestions
import pt.socialfood.core.model.Search

interface SearchRepository {
    suspend fun search(page: Int, limit: Int, query: String? = null): Result<List<Search>>
    suspend fun getRestaurantSuggestions(): Result<RestaurantSuggestions>
    suspend fun getGuideSuggestions(): Result<GuideSuggestions>
}
