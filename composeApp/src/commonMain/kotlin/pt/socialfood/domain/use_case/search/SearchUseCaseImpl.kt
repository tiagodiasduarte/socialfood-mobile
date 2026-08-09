package pt.socialfood.domain.use_case.search

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Search
import pt.socialfood.domain.repository.SearchRepository

class SearchUseCaseImpl(private val repository: SearchRepository) : SearchUseCase {
    override suspend fun invoke(page: Int, limit: Int, query: String?): Result<List<Search>> =
        repository.search(page, limit, query)
}
