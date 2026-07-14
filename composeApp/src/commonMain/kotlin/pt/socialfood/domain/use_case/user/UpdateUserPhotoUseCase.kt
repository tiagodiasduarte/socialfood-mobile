package pt.socialfood.domain.use_case.user

import pt.socialfood.core.Result

interface UpdateUserPhotoUseCase {
    suspend operator fun invoke(id: String, imageUrl: String): Result<Boolean>
}
