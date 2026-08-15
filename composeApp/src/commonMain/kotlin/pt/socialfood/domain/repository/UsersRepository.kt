package pt.socialfood.domain.repository

import kotlinx.coroutines.flow.StateFlow
import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedUsers
import pt.socialfood.domain.model.PresignedUrlData
import pt.socialfood.domain.model.User

interface UsersRepository {
    val currentUser: StateFlow<User?>

    suspend fun clearUser()

    suspend fun saveUser(user: User)

    suspend fun getUsers(): Result<List<User>>

    suspend fun findUsers(page: Int, limit: Int, query: String? = null): Result<PagedUsers>

    suspend fun getUserMe(): Result<User>

    suspend fun findById(id: String): Result<User>

    suspend fun update(
        id: String,
        imageUrl: String? = null,
        name: String? = null,
        username: String? = null,
        facebookUrl: String? = null,
        instagramUrl: String? = null,
        youtubeUrl: String? = null,
        isAuthor: Boolean? = null,
    ): Result<User>

    suspend fun updatePhoto(id: String, imageUrl: String): Result<Boolean>

    suspend fun getPresignedUrl(
        userId: String,
        fileName: String,
        mimeType: String,
        context: String,
    ): Result<PresignedUrlData>
}
