package pt.socialfood.fakes

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedUsers
import pt.socialfood.domain.model.PresignedUrlData
import pt.socialfood.domain.model.User
import pt.socialfood.domain.repository.UsersRepository

class FakeUsersRepository(currentUser: User? = null) : UsersRepository {
    private val _currentUser = MutableStateFlow(currentUser)
    override val currentUser: StateFlow<User?> = _currentUser

    fun emitCurrentUser(user: User?) {
        _currentUser.value = user
    }

    override suspend fun clearUser() {
        _currentUser.value = null
    }

    override suspend fun saveUser(user: User) {
        _currentUser.value = user
    }

    override suspend fun getUsers(): Result<List<User>> = error("not used in this test")

    override suspend fun findUsers(page: Int, limit: Int, query: String?): Result<PagedUsers> =
        error("not used in this test")

    override suspend fun getUserMe(): Result<User> = error("not used in this test")

    override suspend fun findById(id: String): Result<User> = error("not used in this test")

    override suspend fun update(
        id: String,
        imageUrl: String?,
        name: String?,
        username: String?,
        facebookUrl: String?,
        instagramUrl: String?,
        youtubeUrl: String?,
    ): Result<User> = error("not used in this test")

    override suspend fun updatePhoto(id: String, imageUrl: String): Result<Boolean> = error("not used in this test")

    override suspend fun getPresignedUrl(
        userId: String,
        fileName: String,
        mimeType: String,
        context: String,
    ): Result<PresignedUrlData> = error("not used in this test")
}
