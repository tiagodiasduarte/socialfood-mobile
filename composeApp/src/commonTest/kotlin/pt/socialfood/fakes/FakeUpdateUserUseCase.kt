package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.User
import pt.socialfood.domain.usecase.user.UpdateUserUseCase

class FakeUpdateUserUseCase(private val result: Result<User>) : UpdateUserUseCase {
    var invokeCount: Int = 0
        private set
    var lastIsAuthor: Boolean? = null
        private set

    override suspend fun invoke(
        id: String,
        imageUrl: String?,
        name: String?,
        username: String?,
        facebookUrl: String?,
        instagramUrl: String?,
        youtubeUrl: String?,
        isAuthor: Boolean?,
    ): Result<User> {
        invokeCount++
        lastIsAuthor = isAuthor
        return result
    }
}
