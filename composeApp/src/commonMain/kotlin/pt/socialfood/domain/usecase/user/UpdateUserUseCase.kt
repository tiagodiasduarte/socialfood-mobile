package pt.socialfood.domain.usecase.user

import pt.socialfood.core.Result
import pt.socialfood.domain.model.User

interface UpdateUserUseCase {
    suspend operator fun invoke(
        id: String,
        imageUrl: String? = null,
        name: String? = null,
        username: String? = null,
        facebookUrl: String? = null,
        instagramUrl: String? = null,
        youtubeUrl: String? = null,
        isAuthor: Boolean? = null,
    ): Result<User>
}
