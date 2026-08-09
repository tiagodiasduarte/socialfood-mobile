package pt.socialfood.domain.use_case.search

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Search

interface SearchUseCase {
    suspend operator fun invoke(page: Int, limit: Int, query: String? = null): Result<List<Search>>
}
