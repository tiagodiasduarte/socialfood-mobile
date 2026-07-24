package pt.socialfood.fakes

import pt.socialfood.data.UserApi
import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.photo.PresignedUrlRequest
import pt.socialfood.data.network.model.photo.PresignedUrlResponse
import pt.socialfood.data.network.model.user.UpdateUserPhotoRequest
import pt.socialfood.data.network.model.user.UpdateUserRequest
import pt.socialfood.data.network.model.user.UserResponse

class FakeUserApi(private val shouldThrow: Boolean = false) : UserApi {

    private val fakeUserResponse = UserResponse(
        id = "user-id",
        email = "user@test.com",
        name = "Test User",
        imageUrl = null,
        country = "Portugal",
        city = "Lisbon",
        address = null,
        role = "USER",
    )

    override suspend fun getUsers(): List<UserResponse> {
        if (shouldThrow) throw RuntimeException("test error")
        return listOf(fakeUserResponse)
    }

    override suspend fun findUsers(page: Int, limit: Int, query: String?): PagedResponse<UserResponse> {
        if (shouldThrow) throw RuntimeException("test error")
        return PagedResponse(
            items = listOf(fakeUserResponse),
            page = page,
            limit = limit,
            total = 25,
        )
    }

    override suspend fun getUserMe(): UserResponse {
        if (shouldThrow) throw RuntimeException("test error")
        return fakeUserResponse
    }

    override suspend fun findById(id: String): UserResponse {
        if (shouldThrow) throw RuntimeException("test error")
        return fakeUserResponse
    }

    override suspend fun update(request: UpdateUserRequest, id: String): UserResponse {
        if (shouldThrow) throw RuntimeException("test error")
        return fakeUserResponse
    }

    override suspend fun updatePhotoUrl(userId: String, request: UpdateUserPhotoRequest) {
        if (shouldThrow) throw RuntimeException("test error")
    }

    override suspend fun getPresignedUrl(userId: String, request: PresignedUrlRequest): PresignedUrlResponse {
        if (shouldThrow) throw RuntimeException("test error")
        return PresignedUrlResponse(
            uploadUrl = "https://upload.example.com/user-photo",
            key = "user-photo-key",
            publicUrl = "https://public.example.com/user-photo",
        )
    }
}
