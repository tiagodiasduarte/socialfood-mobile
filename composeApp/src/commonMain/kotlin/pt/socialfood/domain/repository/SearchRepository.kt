package pt.socialfood.domain.repository

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Search

interface SearchRepository {
    suspend fun search(page: Int, limit: Int, query: String? = null): Result<List<Search>>
}
