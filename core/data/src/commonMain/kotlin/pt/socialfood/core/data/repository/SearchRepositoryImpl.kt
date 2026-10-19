package pt.socialfood.core.data.repository

import pt.socialfood.core.common.Result
import pt.socialfood.core.data.mapper.toGuideSuggestions
import pt.socialfood.core.data.mapper.toRestaurantSuggestions
import pt.socialfood.core.data.mapper.toSearchResults
import pt.socialfood.core.domain.repository.SearchRepository
import pt.socialfood.core.model.GuideSuggestions
import pt.socialfood.core.model.RestaurantSuggestions
import pt.socialfood.core.model.Search
import pt.socialfood.core.network.api.SearchApi
import pt.socialfood.core.network.safeApiCall

internal class SearchRepositoryImpl(private val searchApi: SearchApi) : SearchRepository {

    override suspend fun search(page: Int, limit: Int, query: String?): Result<List<Search>> =
        safeApiCall { searchApi.search(page, limit, query).toSearchResults() }

    override suspend fun getRestaurantSuggestions(): Result<RestaurantSuggestions> =
        safeApiCall { searchApi.getRestaurantSuggestions().toRestaurantSuggestions() }

    override suspend fun getGuideSuggestions(): Result<GuideSuggestions> =
        safeApiCall { searchApi.getGuideSuggestions().toGuideSuggestions() }
}
