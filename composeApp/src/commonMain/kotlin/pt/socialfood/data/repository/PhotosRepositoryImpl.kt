package pt.socialfood.data.repository

import io.ktor.client.plugins.ResponseException
import kotlinx.io.IOException
import pt.socialfood.core.Result
import pt.socialfood.data.api.S3Api
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
    } catch (e: IOException) {
        Result.Error(e.toErrorEntity())
    } catch (e: ResponseException) {
        Result.Error(e.toErrorEntity())
    } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
        Result.Error(e.toErrorEntity())
    }
}
