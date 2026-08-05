package pt.socialfood.fakes

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import pt.socialfood.core.Result
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.domain.model.PagedGuides
import pt.socialfood.domain.model.PresignedUrlData
import pt.socialfood.domain.repository.GuidesRepository
import pt.socialfood.random.nextGuide
import pt.socialfood.random.nextPagedGuides
import kotlin.random.Random

@Suppress("LongParameterList")
class FakeGuidesRepository(
    private val deleteResult: Result<Boolean> = Result.Success(true),
    private val createResult: Result<Guide> = Result.Success(Random.nextGuide()),
    private val updateResult: Result<Guide> = Result.Success(Random.nextGuide()),
    private val findGuidesResult: Result<List<Guide>> = Result.Success(emptyList()),
    private val findGuidesPagedResult: Result<PagedGuides> = Result.Success(Random.nextPagedGuides()),
    private val findByIdResult: Result<Guide> = Result.Success(Random.nextGuide()),
    private val addRestaurantGuideResult: Result<Guide> = Result.Success(Random.nextGuide()),
    private val guidesPagingFlow: Flow<PagingData<Guide>> = emptyFlow(),
    private val getPhotoPresignedUrlResult: Result<PresignedUrlData> =
        Result.Success(PresignedUrlData(uploadUrl = "https://upload", publicUrl = "https://public")),
    private val addPhotoResult: Result<Boolean> = Result.Success(true),
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

    var findGuidesPagedInvokeCount: Int = 0
        private set
    var lastFindGuidesPagedPage: Int? = null
        private set
    var lastFindGuidesPagedLimit: Int? = null
        private set
    var lastFindGuidesPagedQuery: String? = null
        private set
    var lastFindGuidesPagedUserId: String? = null
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

    var addPhotoInvokeCount: Int = 0
        private set
    var lastAddPhotoImageUrl: String? = null
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

    override suspend fun findGuides(): Result<List<Guide>> = findGuidesResult

    override suspend fun findGuidesPaged(page: Int, limit: Int, query: String?, userId: String?): Result<PagedGuides> {
        findGuidesPagedInvokeCount++
        lastFindGuidesPagedPage = page
        lastFindGuidesPagedLimit = limit
        lastFindGuidesPagedQuery = query
        lastFindGuidesPagedUserId = userId
        return findGuidesPagedResult
    }

    override fun getGuidesPagingFlow(userId: String?): Flow<PagingData<Guide>> {
        lastPagingUserId = userId
        return guidesPagingFlow
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
}
