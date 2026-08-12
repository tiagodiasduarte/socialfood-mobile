package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PresignedUrlData
import pt.socialfood.domain.usecase.user.GetPresignedUrlUseCase

class FakeGetPresignedUrlUseCase(
    private val result: Result<PresignedUrlData>,
) : GetPresignedUrlUseCase {
    var invokeCount: Int = 0
        private set

    override suspend fun invoke(
        userId: String,
        fileName: String,
        mimeType: String,
        context: String,
    ): Result<PresignedUrlData> {
        invokeCount++
        return result
    }
}
