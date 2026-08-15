package pt.socialfood.domain.usecase.user

import pt.socialfood.core.Result
import pt.socialfood.domain.model.User
import pt.socialfood.domain.repository.UsersRepository

class UpdateUserUseCaseImpl(private val repository: UsersRepository) : UpdateUserUseCase {
    override suspend operator fun invoke(
        id: String,
        imageUrl: String?,
        name: String?,
        username: String?,
        facebookUrl: String?,
        instagramUrl: String?,
        youtubeUrl: String?,
        isAuthor: Boolean?,
    ): Result<User> = repository.update(
        id = id,
        imageUrl = imageUrl,
        name = name,
        username = username,
        facebookUrl = facebookUrl,
        instagramUrl = instagramUrl,
        youtubeUrl = youtubeUrl,
        isAuthor = isAuthor,
    )
}
