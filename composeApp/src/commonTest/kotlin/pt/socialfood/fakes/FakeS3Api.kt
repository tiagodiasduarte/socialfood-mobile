package pt.socialfood.fakes

import kotlinx.io.IOException
import pt.socialfood.data.api.S3Api

class FakeS3Api(private val shouldThrow: Boolean = false) : S3Api {

    override suspend fun uploadToS3(uploadUrl: String, bytes: ByteArray, mimeType: String) {
        if (shouldThrow) throw IOException("test error")
    }
}
