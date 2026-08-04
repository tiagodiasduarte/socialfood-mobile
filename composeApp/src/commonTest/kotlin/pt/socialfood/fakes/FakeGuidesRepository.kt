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

class FakeGuidesRepository(
    private val getPhotoPresignedUrlResult: Result<PresignedUrlData> =
        Result.Success(PresignedUrlData(uploadUrl = "https://upload", publicUrl = "https://public")),
    private val addPhotoResult: Result<Boolean> = Result.Success(true),
) : GuidesRepository {
    var addPhotoInvokeCount: Int = 0
        private set
    var lastAddPhotoImageUrl: String? = null
        private set

    override suspend fun delete(id: String): Result<Boolean> = error("not used in this test")

    override suspend fun create(name: String, description: String, userId: String): Result<Guide> =
        error("not used in this test")

    override suspend fun update(
        id: String,
        name: String,
        userId: String,
        description: String,
        restaurantIds: List<String>,
        visibility: GuideVisibility,
    ): Result<Guide> = error("not used in this test")

    override suspend fun findGuides(): Result<List<Guide>> = error("not used in this test")

    override suspend fun findGuidesPaged(page: Int, limit: Int, query: String?, userId: String?): Result<PagedGuides> =
        error("not used in this test")

    override fun getGuidesPagingFlow(userId: String?): Flow<PagingData<Guide>> = emptyFlow()

    override suspend fun findById(id: String): Result<Guide> = error("not used in this test")

    override suspend fun addRestaurantGuide(guideId: String, userId: String, placeId: String?): Result<Guide> =
        error("not used in this test")

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
