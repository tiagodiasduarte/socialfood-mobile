package pt.socialfood.domain.usecase.photo

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PresignedUrlData
import pt.socialfood.domain.repository.PhotosRepository

class UploadPhotoUseCaseImpl(private val photosRepository: PhotosRepository) : UploadPhotoUseCase {

    override suspend fun invoke(presigned: PresignedUrlData, bytes: ByteArray, mimeType: String): Result<Unit> =
        photosRepository.uploadToS3(presigned.uploadUrl, bytes, mimeType)
}
