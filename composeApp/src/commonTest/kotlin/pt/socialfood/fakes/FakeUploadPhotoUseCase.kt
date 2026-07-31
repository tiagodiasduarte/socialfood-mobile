package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PresignedUrlData
import pt.socialfood.domain.use_case.photo.UploadPhotoUseCase

class FakeUploadPhotoUseCase(
    private val result: Result<Unit>,
) : UploadPhotoUseCase {
    var invokeCount: Int = 0
        private set
    var lastPresigned: PresignedUrlData? = null
        private set

    override suspend fun invoke(
        presigned: PresignedUrlData,
        bytes: ByteArray,
        mimeType: String,
    ): Result<Unit> {
        invokeCount++
        lastPresigned = presigned
        return result
    }
}
