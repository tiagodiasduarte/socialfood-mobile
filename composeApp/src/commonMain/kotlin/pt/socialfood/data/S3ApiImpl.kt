package pt.socialfood.data

import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.content.ByteArrayContent

class S3ApiImpl(private val httpClient: HttpClient) : S3Api {

    override suspend fun uploadToS3(uploadUrl: String, bytes: ByteArray, mimeType: String) {
        httpClient.put(uploadUrl) {
            setBody(ByteArrayContent(bytes, ContentType.parse(mimeType)))
            header(HttpHeaders.ContentLength, bytes.size.toString())
        }
    }
}
