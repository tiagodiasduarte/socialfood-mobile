package pt.socialfood.domain.usecase.user

import pt.socialfood.core.Result
import pt.socialfood.domain.repository.UsersRepository

class UpdateUserPhotoUseCaseImpl(private val repository: UsersRepository) : UpdateUserPhotoUseCase {
    override suspend operator fun invoke(id: String, imageUrl: String): Result<Boolean> =
        repository.updatePhoto(id = id, imageUrl = imageUrl)
}
