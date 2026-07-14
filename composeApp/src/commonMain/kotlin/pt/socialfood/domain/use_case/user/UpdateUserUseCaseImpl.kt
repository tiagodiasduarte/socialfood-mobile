package pt.socialfood.domain.use_case.user

import pt.socialfood.core.Result
import pt.socialfood.domain.model.User
import pt.socialfood.domain.repository.UsersRepository

class UpdateUserUseCaseImpl(
    private val repository: UsersRepository,
) : UpdateUserUseCase {
    override suspend operator fun invoke(
        id: String,
        username: String?,
        role: String?,
        imageUrl: String?,
        firstName: String?,
        lastName: String?,
        phoneNumber: String?,
        city: String?,
        country: String?,
        bio: String?,
        facebookUrl: String?,
        instagramUrl: String?,
        youtubeUrl: String?,
    ): Result<User> = repository.update(
        id = id,
        username = username,
        role = role,
        imageUrl = imageUrl,
        firstName = firstName,
        lastName = lastName,
        phoneNumber = phoneNumber,
        city = city,
        country = country,
        bio = bio,
        facebookUrl = facebookUrl,
        instagramUrl = instagramUrl,
        youtubeUrl = youtubeUrl,
    )
}
