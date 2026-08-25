package pt.socialfood.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pt.socialfood.core.Result
import pt.socialfood.data.api.GuidesApi
import pt.socialfood.data.local.dao.GuideDao
import pt.socialfood.data.local.dao.GuideRemoteKeyDao
import pt.socialfood.data.network.model.photo.PresignedUrlRequest
import pt.socialfood.data.paging.GUIDES_ALL_SCOPE
import pt.socialfood.data.paging.GuideCacheTransactionRunner
import pt.socialfood.data.paging.GuideRemoteMediator
import pt.socialfood.domain.error.safeApiCall
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.domain.model.PagedGuides
import pt.socialfood.domain.model.PresignedUrlData
import pt.socialfood.domain.repository.GuidesRepository
import pt.socialfood.mapper.toGuide

private const val GUIDES_PAGE_SIZE = 20

class GuidesRepositoryImpl(
    private val guideApi: GuidesApi,
    private val guideDao: GuideDao,
    private val guideRemoteKeyDao: GuideRemoteKeyDao,
    private val transactionRunner: GuideCacheTransactionRunner,
) : GuidesRepository {

    private var lastGuide: Guide? = null

    override suspend fun create(name: String, description: String, userId: String): Result<Guide> = safeApiCall {
        guideApi.create(
            name = name,
            description = description,
            userId = userId,
        ).toGuide()
    }

    override suspend fun delete(id: String): Result<Boolean> = safeApiCall {
        guideApi.delete(id)
        true
    }.also { result ->
        if (result is Result.Success && lastGuide?.id == id) lastGuide = null
    }

    override suspend fun findGuides(): Result<List<Guide>> = safeApiCall { guideApi.findAll().map { it.toGuide() } }

    override suspend fun findGuidesPaged(page: Int, limit: Int, query: String?, userId: String?): Result<PagedGuides> =
        safeApiCall {
            val response = guideApi.findGuides(page = page, limit = limit, query = query, userId = userId)
            val hasMore = response.page * response.limit < response.total
            PagedGuides(
                guides = response.items.map { it.toGuide() },
                page = response.page,
                total = response.total,
                hasMore = hasMore,
            )
        }

    override suspend fun update(
        id: String,
        name: String,
        userId: String,
        description: String,
        restaurantIds: List<String>,
        visibility: GuideVisibility,
    ): Result<Guide> = safeApiCall {
        guideApi.update(
            id = id,
            name = name,
            userId = userId,
            description = description,
            restaurantIds = restaurantIds,
            visibility = visibility.name,
        ).toGuide()
    }.also { result ->
        if (result is Result.Success) lastGuide = result.data
    }

    override suspend fun findById(id: String): Result<Guide> {
        lastGuide?.takeIf { it.id == id }?.let { return Result.Success(it) }

        return safeApiCall { guideApi.findById(id).toGuide() }.also { result ->
            if (result is Result.Success) lastGuide = result.data
        }
    }

    override suspend fun getPhotoPresignedUrl(
        guideId: String,
        fileName: String,
        mimeType: String,
    ): Result<PresignedUrlData> = safeApiCall {
        val response = guideApi.getGuidePhotoPresignedUrl(
            guideId = guideId,
            request = PresignedUrlRequest(
                fileName = fileName,
                mimeType = mimeType,
                context = "guide",
            ),
        )
        PresignedUrlData(
            uploadUrl = response.uploadUrl,
            publicUrl = response.publicUrl,
        )
    }

    override suspend fun addRestaurantGuide(guideId: String, userId: String, placeId: String?): Result<Guide> =
        safeApiCall {
            guideApi.addRestaurantGuide(
                guideId = guideId,
                placeId = placeId,
            ).toGuide()
        }.also { result ->
            if (result is Result.Success) lastGuide = result.data
        }

    override suspend fun addPhoto(guideId: String, imageUrl: String): Result<Boolean> = safeApiCall {
        guideApi.addPhoto(
            guideId = guideId,
            imageUrl = imageUrl,
        )
        true
    }.also { result ->
        if (result is Result.Success && lastGuide?.id == guideId) lastGuide = null
    }

    override suspend fun deletePhoto(guideId: String): Result<Boolean> = safeApiCall {
        guideApi.deletePhoto(guideId = guideId)
        true
    }.also { result ->
        if (result is Result.Success && lastGuide?.id == guideId) lastGuide = null
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getGuidesPagingFlow(userId: String?): Flow<PagingData<Guide>> {
        val scope = userId ?: GUIDES_ALL_SCOPE
        return Pager(
            config = PagingConfig(pageSize = GUIDES_PAGE_SIZE),
            remoteMediator = GuideRemoteMediator(
                scope = scope,
                guidesApi = guideApi,
                guideDao = guideDao,
                guideRemoteKeyDao = guideRemoteKeyDao,
                transactionRunner = transactionRunner,
            ),
            pagingSourceFactory = { guideDao.pagingSource(scope) },
        ).flow.map { pagingData -> pagingData.map { it.toGuide() } }
    }
}
