package pt.socialfood.data.repository

import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.io.IOException
import pt.socialfood.core.Result
import pt.socialfood.data.api.UserApi
import pt.socialfood.data.network.extensions.toErrorEntity
import pt.socialfood.data.network.model.photo.PresignedUrlRequest
import pt.socialfood.data.network.model.user.UpdateUserPhotoRequest
import pt.socialfood.data.network.model.user.UpdateUserRequest
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
        return try {
            val users = userApi.getUsers().map { it.toUser() }
            Result.Success(users)
        } catch (e: IOException) {
            Result.Error(e.toErrorEntity())
        } catch (e: ResponseException) {
            Result.Error(e.toErrorEntity())
        }
    }

    override suspend fun findUsers(page: Int, limit: Int, query: String?): Result<PagedUsers> {
        return try {
            val response = userApi.findUsers(page = page, limit = limit, query = query)
            val hasMore = response.page * response.limit < response.total
            Result.Success(
                PagedUsers(
                    users = response.items.map { it.toUser() },
                    page = response.page,
                    total = response.total,
                    hasMore = hasMore,
                )
            )
        } catch (e: IOException) {
            Result.Error(e.toErrorEntity())
        } catch (e: ResponseException) {
            Result.Error(e.toErrorEntity())
        }
    }

    override suspend fun getUserMe(): Result<User> {
        return try {
            val user = userApi.getUserMe().toUser()
            _currentUser.value = user
            Result.Success(user)
        } catch (e: IOException) {
            Result.Error(e.toErrorEntity())
        } catch (e: ResponseException) {
            Result.Error(e.toErrorEntity())
        }
    }

    override suspend fun findById(id: String): Result<User> {
        return try {
            Result.Success(userApi.findById(id).toUser())
        } catch (e: IOException) {
            Result.Error(e.toErrorEntity())
        } catch (e: ResponseException) {
            Result.Error(e.toErrorEntity())
        }
    }

    override suspend fun update(
        id: String,
        role: String?,
        imageUrl: String?,
        name: String?,
        city: String?,
        country: String?,
        facebookUrl: String?,
        instagramUrl: String?,
        youtubeUrl: String?,
    ): Result<User> {
        return try {
            val request = UpdateUserRequest(
                role = role,
                name = name,
                city = city,
                country = country,
                facebookUrl = facebookUrl,
                instagramUrl = instagramUrl,
                youtubeUrl = youtubeUrl,
            )
            val user = userApi.update(request, id).toUser()
            _currentUser.value = user
            Result.Success(user)
        } catch (e: IOException) {
            Result.Error(e.toErrorEntity())
        } catch (e: ResponseException) {
            Result.Error(e.toErrorEntity())
        }
    }

    override suspend fun updatePhoto(id: String, imageUrl: String): Result<Boolean> {
        return try {
            userApi.updatePhotoUrl(id, UpdateUserPhotoRequest(imageUrl))
            Result.Success(true)
        } catch (e: IOException) {
            Result.Error(e.toErrorEntity())
        } catch (e: ResponseException) {
            Result.Error(e.toErrorEntity())
        }
    }

    override suspend fun getPresignedUrl(
        userId: String,
        fileName: String,
        mimeType: String,
        context: String,
    ): Result<PresignedUrlData> = try {
        val response = userApi.getPresignedUrl(
            userId = userId,
            request = PresignedUrlRequest(
                fileName = fileName,
                mimeType = mimeType,
                context = context
            )
        )
        Result.Success(
            PresignedUrlData(
                uploadUrl = response.uploadUrl,
                publicUrl = response.publicUrl
            )
        )
    } catch (e: IOException) {
        Result.Error(e.toErrorEntity())
    } catch (e: ResponseException) {
        Result.Error(e.toErrorEntity())
    }
}
