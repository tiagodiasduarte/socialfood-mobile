package pt.socialfood.data.repository

import pt.socialfood.core.Result
import pt.socialfood.data.S3Api
import pt.socialfood.data.network.extensions.toErrorEntity
import pt.socialfood.domain.repository.PhotosRepository

class PhotosRepositoryImpl(
    private val s3Api: S3Api,
) : PhotosRepository {

    override suspend fun uploadToS3(
        uploadUrl: String,
        bytes: ByteArray,
        mimeType: String,
    ): Result<Unit> = try {
        s3Api.uploadToS3(uploadUrl, bytes, mimeType)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e.toErrorEntity())
    }
}
