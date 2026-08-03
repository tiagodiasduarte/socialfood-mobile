package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.AuthorDetail
import pt.socialfood.domain.use_case.author.GetAuthorByIdUseCase

class FakeGetAuthorByIdUseCase(private val result: Result<AuthorDetail>) : GetAuthorByIdUseCase {
    var invokeCount: Int = 0
        private set

    override suspend fun invoke(id: String): Result<AuthorDetail> {
        invokeCount++
        return result
    }
}
