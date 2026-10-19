package pt.socialfood.core.domain.repository

import pt.socialfood.core.common.Result

interface PhotosRepository {
    suspend fun uploadToS3(uploadUrl: String, bytes: ByteArray, mimeType: String): Result<Unit>
}
