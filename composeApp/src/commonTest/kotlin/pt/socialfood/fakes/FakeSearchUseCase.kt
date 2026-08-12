package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Search
import pt.socialfood.domain.usecase.search.SearchUseCase

class FakeSearchUseCase(private val result: Result<List<Search>> = Result.Success(emptyList())) : SearchUseCase {
    var invokeCount: Int = 0
        private set

    override suspend fun invoke(page: Int, limit: Int, query: String?): Result<List<Search>> {
        invokeCount++
        return result
    }
}
