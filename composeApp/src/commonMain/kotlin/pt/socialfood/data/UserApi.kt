package pt.socialfood.data

import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.photo.PresignedUrlRequest
import pt.socialfood.data.network.model.photo.PresignedUrlResponse
import pt.socialfood.data.network.model.user.UpdateUserPhotoRequest
import pt.socialfood.data.network.model.user.UpdateUserRequest
import pt.socialfood.data.network.model.user.UserResponse

interface UserApi {
    suspend fun getUsers(): List<UserResponse>

    suspend fun findUsers(page: Int, limit: Int, query: String? = null): PagedResponse<UserResponse>

    suspend fun getUserMe(): UserResponse

    suspend fun findById(id: String): UserResponse

    suspend fun update(request: UpdateUserRequest, id: String): UserResponse

    suspend fun updatePhotoUrl(userId: String, request: UpdateUserPhotoRequest)

    suspend fun getPresignedUrl(userId: String, request: PresignedUrlRequest): PresignedUrlResponse
}
