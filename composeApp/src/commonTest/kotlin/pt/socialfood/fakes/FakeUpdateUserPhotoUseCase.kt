package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.use_case.user.UpdateUserPhotoUseCase

class FakeUpdateUserPhotoUseCase(
    private val result: Result<Boolean>,
) : UpdateUserPhotoUseCase {
    var invokeCount: Int = 0
        private set
    var lastImageUrl: String? = null
        private set

    override suspend fun invoke(id: String, imageUrl: String): Result<Boolean> {
        invokeCount++
        lastImageUrl = imageUrl
        return result
    }
}