package pt.socialfood.data.repository

import pt.socialfood.core.Result
import pt.socialfood.data.api.S3Api
import pt.socialfood.domain.error.safeApiCall
import pt.socialfood.domain.repository.PhotosRepository

class PhotosRepositoryImpl(private val s3Api: S3Api) : PhotosRepository {

    override suspend fun uploadToS3(uploadUrl: String, bytes: ByteArray, mimeType: String): Result<Unit> = safeApiCall {
        s3Api.uploadToS3(uploadUrl, bytes, mimeType)
    }
}
