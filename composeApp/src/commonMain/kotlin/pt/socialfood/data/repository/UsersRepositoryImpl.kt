package pt.socialfood.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pt.socialfood.core.Result
import pt.socialfood.data.api.UserApi
import pt.socialfood.data.network.model.photo.PresignedUrlRequest
import pt.socialfood.data.network.model.user.UpdateUserPhotoRequest
import pt.socialfood.data.network.model.user.UpdateUserRequest
import pt.socialfood.domain.error.safeApiCall
import pt.socialfood.domain.model.PagedUsers
import pt.socialfood.domain.model.PresignedUrlData
import pt.socialfood.domain.model.User
import pt.socialfood.domain.repository.UsersRepository
import pt.socialfood.mapper.toUser

class UsersRepositoryImpl(
    private val userApi: UserApi,
) : UsersRepository {

    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: StateFlow<User?> = _currentUser

    override suspend fun clearUser() {
        _currentUser.value = null
    }

    override suspend fun saveUser(user: User) {
        _currentUser.value = user
    }

    override suspend fun getUsers(): Result<List<User>> {
        return safeApiCall { userApi.getUsers().map { it.toUser() } }
    }

    override suspend fun findUsers(page: Int, limit: Int, query: String?): Result<PagedUsers> {
        return safeApiCall {
            val response = userApi.findUsers(page = page, limit = limit, query = query)
            val hasMore = response.page * response.limit < response.total
            PagedUsers(
                users = response.items.map { it.toUser() },
                page = response.page,
                total = response.total,
                hasMore = hasMore,
            )
        }
    }

    override suspend fun getUserMe(): Result<User> {
        return safeApiCall {
            val user = userApi.getUserMe().toUser()
            _currentUser.value = user
            user
        }
    }

    override suspend fun findById(id: String): Result<User> {
        return safeApiCall { userApi.findById(id).toUser() }
    }

    override suspend fun update(
        id: String,
        imageUrl: String?,
        name: String?,
        username: String?,
        facebookUrl: String?,
        instagramUrl: String?,
        youtubeUrl: String?,
        isAuthor: Boolean?,
    ): Result<User> {
        return safeApiCall {
            val request = UpdateUserRequest(
                name = name,
                username = username,
                facebookUrl = facebookUrl,
                instagramUrl = instagramUrl,
                youtubeUrl = youtubeUrl,
                isAuthor = isAuthor,
            )
            val user = userApi.update(request, id).toUser()
            _currentUser.value = user
            user
        }
    }

    override suspend fun updatePhoto(id: String, imageUrl: String): Result<Boolean> {
        return safeApiCall {
            userApi.updatePhotoUrl(id, UpdateUserPhotoRequest(imageUrl))
            true
        }
    }

    override suspend fun getPresignedUrl(
        userId: String,
        fileName: String,
        mimeType: String,
        context: String,
    ): Result<PresignedUrlData> = safeApiCall {
        val response = userApi.getPresignedUrl(
            userId = userId,
            request = PresignedUrlRequest(
                fileName = fileName,
                mimeType = mimeType,
                context = context
            )
        )
        PresignedUrlData(
            uploadUrl = response.uploadUrl,
            publicUrl = response.publicUrl
        )
    }
}
