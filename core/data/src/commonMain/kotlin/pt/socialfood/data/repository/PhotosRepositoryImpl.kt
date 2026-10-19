package pt.socialfood.data.repository

import pt.socialfood.core.common.Result
import pt.socialfood.core.domain.repository.PhotosRepository
import pt.socialfood.data.api.S3Api
import pt.socialfood.domain.error.safeApiCall

internal class PhotosRepositoryImpl(private val s3Api: S3Api) : PhotosRepository {

    override suspend fun uploadToS3(uploadUrl: String, bytes: ByteArray, mimeType: String): Result<Unit> = safeApiCall {
        s3Api.uploadToS3(uploadUrl, bytes, mimeType)
    }
}
