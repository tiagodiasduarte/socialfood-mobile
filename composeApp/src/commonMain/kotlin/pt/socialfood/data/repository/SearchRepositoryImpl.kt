package pt.socialfood.data.repository

import pt.socialfood.core.Result
import pt.socialfood.data.api.SearchApi
import pt.socialfood.domain.error.safeApiCall
import pt.socialfood.domain.model.Search
import pt.socialfood.domain.repository.SearchRepository
import pt.socialfood.mapper.toSearchResults

class SearchRepositoryImpl(private val searchApi: SearchApi) : SearchRepository {

    override suspend fun search(page: Int, limit: Int, query: String?): Result<List<Search>> =
        safeApiCall { searchApi.search(page, limit, query).toSearchResults() }
}
