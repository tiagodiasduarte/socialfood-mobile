package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.AuthorDetail
import pt.socialfood.domain.usecase.author.GetUserAuthorProfileUseCase

class FakeGetUserAuthorProfileUseCase(private val result: Result<AuthorDetail>) : GetUserAuthorProfileUseCase {
    var invokeCount: Int = 0
        private set

    override suspend fun invoke(): Result<AuthorDetail> {
        invokeCount++
        return result
    }
}
