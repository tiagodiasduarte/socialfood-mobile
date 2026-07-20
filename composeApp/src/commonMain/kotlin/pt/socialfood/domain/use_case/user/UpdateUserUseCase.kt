package pt.socialfood.domain.use_case.user

import pt.socialfood.core.Result
import pt.socialfood.domain.model.User

interface UpdateUserUseCase {
    suspend operator fun invoke(
        id: String,
        role: String? = null,
        imageUrl: String? = null,
        name: String? = null,
        city: String? = null,
        country: String? = null,
        bio: String? = null,
        facebookUrl: String? = null,
        instagramUrl: String? = null,
        youtubeUrl: String? = null,
    ): Result<User>
}
