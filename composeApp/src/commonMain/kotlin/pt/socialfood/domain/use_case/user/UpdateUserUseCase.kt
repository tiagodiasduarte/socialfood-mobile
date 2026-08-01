package pt.socialfood.domain.use_case.user

import pt.socialfood.core.Result
import pt.socialfood.domain.model.User

@Suppress("LongParameterList")
interface UpdateUserUseCase {
    suspend operator fun invoke(
        id: String,
        imageUrl: String? = null,
        name: String? = null,
        username: String? = null,
        facebookUrl: String? = null,
        instagramUrl: String? = null,
        youtubeUrl: String? = null,
    ): Result<User>
}
