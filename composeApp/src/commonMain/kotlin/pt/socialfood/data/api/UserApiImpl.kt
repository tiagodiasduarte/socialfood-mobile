package pt.socialfood.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.photo.PresignedUrlRequest
import pt.socialfood.data.network.model.photo.PresignedUrlResponse
import pt.socialfood.data.network.model.user.UpdateUserPhotoRequest
import pt.socialfood.data.network.model.user.UpdateUserRequest
import pt.socialfood.data.network.model.user.UserResponse

class UserApiImpl(
    private val client: HttpClient
) : UserApi {
    override suspend fun getUsers(): List<UserResponse> =
        client.get("users/all").body()

    override suspend fun findUsers(page: Int, limit: Int, query: String?): PagedResponse<UserResponse> =
        client.get("users/all") {
            parameter("page", page)
            parameter("limit", limit)
            if (!query.isNullOrBlank()) parameter("search", query)
        }.body()

    override suspend fun getUserMe(): UserResponse =
        client.get("users/me").body()

    override suspend fun findById(id: String): UserResponse =
        client.get("users/$id").body()

    override suspend fun update(request: UpdateUserRequest, id: String): UserResponse =
        client.put("users/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    override suspend fun updatePhotoUrl(userId: String, request: UpdateUserPhotoRequest) {
        client.post("users/$userId/photo") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    override suspend fun getPresignedUrl(
        userId: String,
        request: PresignedUrlRequest,
    ): PresignedUrlResponse =
        client.post("users/$userId/photo/presigned-url") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
}
