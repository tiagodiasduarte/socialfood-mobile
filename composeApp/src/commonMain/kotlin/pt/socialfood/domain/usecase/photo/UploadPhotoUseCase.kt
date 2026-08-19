package pt.socialfood.domain.usecase.photo

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PresignedUrlData

interface UploadPhotoUseCase {
    suspend operator fun invoke(presigned: PresignedUrlData, bytes: ByteArray, mimeType: String): Result<Unit>
}
