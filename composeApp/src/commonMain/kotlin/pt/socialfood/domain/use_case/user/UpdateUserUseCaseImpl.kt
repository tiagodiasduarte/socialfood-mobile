package pt.socialfood.domain.use_case.user

import pt.socialfood.core.Result
import pt.socialfood.domain.model.User
import pt.socialfood.domain.repository.UsersRepository

class UpdateUserUseCaseImpl(
    private val repository: UsersRepository,
) : UpdateUserUseCase {
    override suspend operator fun invoke(
        id: String,
        role: String?,
        imageUrl: String?,
        name: String?,
        city: String?,
        country: String?,
        facebookUrl: String?,
        instagramUrl: String?,
        youtubeUrl: String?,
    ): Result<User> = repository.update(
        id = id,
        role = role,
        imageUrl = imageUrl,
        name = name,
        city = city,
        country = country,
        facebookUrl = facebookUrl,
        instagramUrl = instagramUrl,
        youtubeUrl = youtubeUrl,
    )
}
