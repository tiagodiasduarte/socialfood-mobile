package pt.socialfood.domain.use_case.photo

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PresignedUrlData

interface UploadPhotoUseCase {
    suspend operator fun invoke(
        presigned: PresignedUrlData,
        bytes: ByteArray,
        mimeType: String,
    ): Result<Unit>
}
