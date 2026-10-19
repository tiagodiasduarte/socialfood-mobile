package pt.socialfood.core.testing.fakes

import pt.socialfood.core.common.Result
import pt.socialfood.core.domain.repository.PhotosRepository

class FakePhotosRepository(private val uploadResult: Result<Unit> = Result.Success(Unit)) : PhotosRepository {
    var uploadInvokeCount: Int = 0
        private set
    var lastUploadUrl: String? = null
        private set
    var lastMimeType: String? = null
        private set

    override suspend fun uploadToS3(uploadUrl: String, bytes: ByteArray, mimeType: String): Result<Unit> {
        uploadInvokeCount++
        lastUploadUrl = uploadUrl
        lastMimeType = mimeType
        return uploadResult
    }
}
