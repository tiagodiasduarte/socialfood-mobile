package pt.socialfood.data.api

interface S3Api {
    suspend fun uploadToS3(uploadUrl: String, bytes: ByteArray, mimeType: String)
}
