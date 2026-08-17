package pt.socialfood.domain.usecase.user

import pt.socialfood.core.Result

interface UpdateUserPhotoUseCase {
    suspend operator fun invoke(id: String, imageUrl: String): Result<Boolean>
}
