package pt.socialfood.data.repository

import pt.socialfood.core.common.Result
import pt.socialfood.core.domain.repository.SearchRepository
import pt.socialfood.core.model.GuideSuggestions
import pt.socialfood.core.model.RestaurantSuggestions
import pt.socialfood.core.model.Search
import pt.socialfood.data.api.SearchApi
import pt.socialfood.domain.error.safeApiCall
import pt.socialfood.mapper.toGuideSuggestions
import pt.socialfood.mapper.toRestaurantSuggestions
import pt.socialfood.mapper.toSearchResults

internal class SearchRepositoryImpl(private val searchApi: SearchApi) : SearchRepository {

    override suspend fun search(page: Int, limit: Int, query: String?): Result<List<Search>> =
        safeApiCall { searchApi.search(page, limit, query).toSearchResults() }

    override suspend fun getRestaurantSuggestions(): Result<RestaurantSuggestions> =
        safeApiCall { searchApi.getRestaurantSuggestions().toRestaurantSuggestions() }

    override suspend fun getGuideSuggestions(): Result<GuideSuggestions> =
        safeApiCall { searchApi.getGuideSuggestions().toGuideSuggestions() }
}
