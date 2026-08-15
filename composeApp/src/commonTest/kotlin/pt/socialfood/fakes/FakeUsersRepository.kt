package pt.socialfood.fakes

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedUsers
import pt.socialfood.domain.model.PresignedUrlData
import pt.socialfood.domain.model.User
import pt.socialfood.domain.repository.UsersRepository
import pt.socialfood.random.nextPagedUsers
import pt.socialfood.random.nextPresignedUrlData
import pt.socialfood.random.nextUser
import kotlin.random.Random

class FakeUsersRepository(
    currentUser: User? = null,
    private val getUsersResult: Result<List<User>> = Result.Success(emptyList()),
    private val findUsersResult: Result<PagedUsers> = Result.Success(Random.nextPagedUsers()),
    private val getUserMeResult: Result<User> = Result.Success(Random.nextUser()),
    private val findByIdResult: Result<User> = Result.Success(Random.nextUser()),
    private val updateResult: Result<User> = Result.Success(Random.nextUser()),
    private val updatePhotoResult: Result<Boolean> = Result.Success(true),
    private val getPresignedUrlResult: Result<PresignedUrlData> = Result.Success(Random.nextPresignedUrlData()),
) : UsersRepository {
    private val _currentUser = MutableStateFlow(currentUser)
    override val currentUser: StateFlow<User?> = _currentUser

    fun emitCurrentUser(user: User?) {
        _currentUser.value = user
    }

    var clearUserInvokeCount: Int = 0
        private set

    var lastSavedUser: User? = null
        private set

    var lastFindUsersPage: Int? = null
        private set
    var lastFindUsersLimit: Int? = null
        private set
    var lastFindUsersQuery: String? = null
        private set

    var lastFindByIdId: String? = null
        private set

    var updateInvokeCount: Int = 0
        private set
    var lastUpdateId: String? = null
        private set
    var lastUpdateImageUrl: String? = null
        private set
    var lastUpdateName: String? = null
        private set
    var lastUpdateUsername: String? = null
        private set
    var lastUpdateFacebookUrl: String? = null
        private set
    var lastUpdateInstagramUrl: String? = null
        private set
    var lastUpdateYoutubeUrl: String? = null
        private set

    var updatePhotoInvokeCount: Int = 0
        private set
    var lastUpdatePhotoId: String? = null
        private set
    var lastUpdatePhotoImageUrl: String? = null
        private set

    var lastPresignedUrlUserId: String? = null
        private set
    var lastPresignedUrlFileName: String? = null
        private set
    var lastPresignedUrlMimeType: String? = null
        private set
    var lastPresignedUrlContext: String? = null
        private set

    override suspend fun clearUser() {
        clearUserInvokeCount++
        _currentUser.value = null
    }

    override suspend fun saveUser(user: User) {
        lastSavedUser = user
        _currentUser.value = user
    }

    override suspend fun getUsers(): Result<List<User>> = getUsersResult

    override suspend fun findUsers(page: Int, limit: Int, query: String?): Result<PagedUsers> {
        lastFindUsersPage = page
        lastFindUsersLimit = limit
        lastFindUsersQuery = query
        return findUsersResult
    }

    override suspend fun getUserMe(): Result<User> = getUserMeResult

    override suspend fun findById(id: String): Result<User> {
        lastFindByIdId = id
        return findByIdResult
    }

    override suspend fun update(
        id: String,
        imageUrl: String?,
        name: String?,
        username: String?,
        facebookUrl: String?,
        instagramUrl: String?,
        youtubeUrl: String?,
    ): Result<User> {
        updateInvokeCount++
        lastUpdateId = id
        lastUpdateImageUrl = imageUrl
        lastUpdateName = name
        lastUpdateUsername = username
        lastUpdateFacebookUrl = facebookUrl
        lastUpdateInstagramUrl = instagramUrl
        lastUpdateYoutubeUrl = youtubeUrl
        return updateResult
    }

    override suspend fun updatePhoto(id: String, imageUrl: String): Result<Boolean> {
        updatePhotoInvokeCount++
        lastUpdatePhotoId = id
        lastUpdatePhotoImageUrl = imageUrl
        return updatePhotoResult
    }

    override suspend fun getPresignedUrl(
        userId: String,
        fileName: String,
        mimeType: String,
        context: String,
    ): Result<PresignedUrlData> {
        lastPresignedUrlUserId = userId
        lastPresignedUrlFileName = fileName
        lastPresignedUrlMimeType = mimeType
        lastPresignedUrlContext = context
        return getPresignedUrlResult
    }
}
