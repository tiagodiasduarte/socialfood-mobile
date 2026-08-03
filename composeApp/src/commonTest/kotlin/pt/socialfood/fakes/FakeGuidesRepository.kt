package pt.socialfood.fakes

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.domain.model.PagedGuides
import pt.socialfood.domain.model.PresignedUrlData
import pt.socialfood.domain.repository.GuidesRepository

class FakeGuidesRepository(
    private val getPhotoPresignedUrlResult: Result<PresignedUrlData> =
        Result.Failure(DataError.Network(Exception("test error"))),
    private val addPhotoResult: Result<Boolean> = Result.Success(true),
) : GuidesRepository {
    override suspend fun delete(id: String): Result<Boolean> = Result.Success(true)

    override suspend fun create(name: String, description: String, userId: String): Result<Guide> =
        Result.Failure(DataError.Network(Exception("test error")))

    override suspend fun update(
        id: String,
        name: String,
        userId: String,
        description: String,
        restaurantIds: List<String>,
        visibility: GuideVisibility,
    ): Result<Guide> = Result.Failure(DataError.Network(Exception("test error")))

    override suspend fun findGuides(): Result<List<Guide>> = Result.Success(emptyList())

    override suspend fun findGuidesPaged(page: Int, limit: Int, query: String?, userId: String?): Result<PagedGuides> =
        Result.Failure(DataError.Network(Exception("test error")))

    override fun getGuidesPagingFlow(userId: String?): Flow<PagingData<Guide>> = flowOf(PagingData.empty())

    override suspend fun findById(id: String): Result<Guide> =
        Result.Failure(DataError.Network(Exception("test error")))

    override suspend fun addRestaurantGuide(guideId: String, userId: String, placeId: String?): Result<Guide> =
        Result.Failure(DataError.Network(Exception("test error")))

    override suspend fun addPhoto(guideId: String, imageUrl: String): Result<Boolean> = addPhotoResult

    override suspend fun deletePhoto(guideId: String): Result<Boolean> = Result.Success(true)

    override suspend fun getPhotoPresignedUrl(
        guideId: String,
        fileName: String,
        mimeType: String,
    ): Result<PresignedUrlData> = getPhotoPresignedUrlResult
}
