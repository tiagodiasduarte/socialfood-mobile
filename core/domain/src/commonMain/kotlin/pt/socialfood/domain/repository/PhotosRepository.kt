package pt.socialfood.domain.repository

import pt.socialfood.core.Result

interface PhotosRepository {
    suspend fun uploadToS3(uploadUrl: String, bytes: ByteArray, mimeType: String): Result<Unit>
}
