package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.User
import pt.socialfood.domain.usecase.user.GetUserMeUseCase

class FakeGetUserMeUseCase(private val result: Result<User>) : GetUserMeUseCase {
    var invokeCount: Int = 0
        private set

    override suspend fun invoke(): Result<User> {
        invokeCount++
        return result
    }
}
