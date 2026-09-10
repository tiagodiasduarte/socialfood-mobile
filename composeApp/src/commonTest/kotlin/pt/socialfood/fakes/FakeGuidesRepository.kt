package pt.socialfood.fakes

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import pt.socialfood.core.Result
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.domain.model.PresignedUrlData
import pt.socialfood.domain.repository.GuidesRepository
import pt.socialfood.random.nextGuide
import kotlin.random.Random

class FakeGuidesRepository(
    private val deleteResult: Result<Boolean> = Result.Success(true),
    private val createResult: Result<Guide> = Result.Success(Random.nextGuide()),
    private val updateResult: Result<Guide> = Result.Success(Random.nextGuide()),
    private val findByIdResult: Result<Guide> = Result.Success(Random.nextGuide()),
    private val addRestaurantGuideResult: Result<Guide> = Result.Success(Random.nextGuide()),
    private val guidesPagingFlow: Flow<PagingData<Guide>> = emptyFlow(),
    private val joinedGuidesPagingFlow: Flow<PagingData<Guide>> = emptyFlow(),
    private val getPhotoPresignedUrlResult: Result<PresignedUrlData> =
        Result.Success(PresignedUrlData(uploadUrl = "https://upload", publicUrl = "https://public")),
    private val addPhotoResult: Result<Boolean> = Result.Success(true),
    private val findGuideBySharedCodeResult: Result<Guide> = Result.Success(Random.nextGuide()),
    private val joinGuideResult: Result<Guide> = Result.Success(Random.nextGuide()),
    private val leaveGuideResult: Result<Boolean> = Result.Success(true),
) : GuidesRepository {
    var deleteInvokeCount: Int = 0
        private set
    var lastDeleteId: String? = null
        private set

    var createInvokeCount: Int = 0
        private set
    var lastCreateName: String? = null
        private set
    var lastCreateDescription: String? = null
        private set
    var lastCreateUserId: String? = null
        private set

    var updateInvokeCount: Int = 0
        private set
    var lastUpdateId: String? = null
        private set
    var lastUpdateName: String? = null
        private set
    var lastUpdateUserId: String? = null
        private set
    var lastUpdateDescription: String? = null
        private set
    var lastUpdateRestaurantIds: List<String>? = null
        private set
    var lastUpdateVisibility: GuideVisibility? = null
        private set

    var lastFindByIdId: String? = null
        private set

    var addRestaurantGuideInvokeCount: Int = 0
        private set
    var lastAddRestaurantGuideId: String? = null
        private set
    var lastAddRestaurantUserId: String? = null
        private set
    var lastAddRestaurantPlaceId: String? = null
        private set

    var lastPagingUserId: String? = null
        private set

    var lastJoinedPagingUserId: String? = null
        private set

    var addPhotoInvokeCount: Int = 0
        private set
    var lastAddPhotoImageUrl: String? = null
        private set

    var findGuideBySharedCodeInvokeCount: Int = 0
        private set
    var lastFindGuideBySharedCodeCode: String? = null
        private set

    var joinGuideInvokeCount: Int = 0
        private set
    var lastJoinGuideId: String? = null
        private set
    var lastJoinGuideCode: String? = null
        private set

    var leaveGuideInvokeCount: Int = 0
        private set
    var lastLeaveGuideId: String? = null
        private set

    override suspend fun delete(id: String): Result<Boolean> {
        deleteInvokeCount++
        lastDeleteId = id
        return deleteResult
    }

    override suspend fun create(name: String, description: String, userId: String): Result<Guide> {
        createInvokeCount++
        lastCreateName = name
        lastCreateDescription = description
        lastCreateUserId = userId
        return createResult
    }

    override suspend fun update(
        id: String,
        name: String,
        userId: String,
        description: String,
        restaurantIds: List<String>,
        visibility: GuideVisibility,
    ): Result<Guide> {
        updateInvokeCount++
        lastUpdateId = id
        lastUpdateName = name
        lastUpdateUserId = userId
        lastUpdateDescription = description
        lastUpdateRestaurantIds = restaurantIds
        lastUpdateVisibility = visibility
        return updateResult
    }

    override suspend fun findGuideBySharedCode(code: String): Result<Guide> {
        findGuideBySharedCodeInvokeCount++
        lastFindGuideBySharedCodeCode = code
        return findGuideBySharedCodeResult
    }

    override fun findGuidesPagingFlow(): Flow<PagingData<Guide>> = guidesPagingFlow

    override fun findUserGuidesPagingFlow(userId: String): Flow<PagingData<Guide>> {
        lastPagingUserId = userId
        return guidesPagingFlow
    }

    override fun findUserJoinedGuidesPagingFlow(userId: String): Flow<PagingData<Guide>> {
        lastJoinedPagingUserId = userId
        return joinedGuidesPagingFlow
    }

    override suspend fun findById(id: String): Result<Guide> {
        lastFindByIdId = id
        return findByIdResult
    }

    override suspend fun addRestaurantGuide(guideId: String, userId: String, placeId: String?): Result<Guide> {
        addRestaurantGuideInvokeCount++
        lastAddRestaurantGuideId = guideId
        lastAddRestaurantUserId = userId
        lastAddRestaurantPlaceId = placeId
        return addRestaurantGuideResult
    }

    override suspend fun addPhoto(guideId: String, imageUrl: String): Result<Boolean> {
        addPhotoInvokeCount++
        lastAddPhotoImageUrl = imageUrl
        return addPhotoResult
    }

    override suspend fun deletePhoto(guideId: String): Result<Boolean> = error("not used in this test")

    override suspend fun getPhotoPresignedUrl(
        guideId: String,
        fileName: String,
        mimeType: String,
    ): Result<PresignedUrlData> = getPhotoPresignedUrlResult

    override suspend fun joinGuide(guideId: String, code: String): Result<Guide> {
        joinGuideInvokeCount++
        lastJoinGuideId = guideId
        lastJoinGuideCode = code
        return joinGuideResult
    }

    override suspend fun leaveGuide(guideId: String): Result<Boolean> {
        leaveGuideInvokeCount++
        lastLeaveGuideId = guideId
        return leaveGuideResult
    }
}
