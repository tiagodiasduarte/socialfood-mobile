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
import pt.socialfood.data.network.extensions.toErrorEntity
import pt.socialfood.data.network.model.photo.PresignedUrlRequest
import pt.socialfood.data.paging.GUIDES_ALL_SCOPE
import pt.socialfood.data.paging.GuideCacheTransactionRunner
import pt.socialfood.data.paging.GuideRemoteMediator
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

    override suspend fun create(
        name: String,
        description: String,
        userId: String,
    ): Result<Guide> {
        return try {
            val guide = guideApi.create(
                name = name,
                description = description,
                userId = userId
            ).toGuide()
            Result.Success(guide)
        } catch (exception: Exception) {
            Result.Error(exception.toErrorEntity())
        }
    }

    override suspend fun delete(id: String): Result<Boolean> {
        return try {
            guideApi.delete(id)
            Result.Success(true)

        } catch (exception: Exception) {
            Result.Error(exception.toErrorEntity())
        }
    }

    override suspend fun findGuides(): Result<List<Guide>> {
        return try {
            val guides = guideApi.findAll().map { it.toGuide() }
            Result.Success(guides)
        } catch (exception: Exception) {
            Result.Error(exception.toErrorEntity())
        }
    }

    override suspend fun findGuidesPaged(page: Int, limit: Int, query: String?, userId: String?): Result<PagedGuides> {
        return try {
            val response = guideApi.findGuides(page = page, limit = limit, query = query, userId = userId)
            val hasMore = response.page * response.limit < response.total
            Result.Success(
                PagedGuides(
                    guides = response.items.map { it.toGuide() },
                    page = response.page,
                    total = response.total,
                    hasMore = hasMore,
                )
            )
        } catch (exception: Exception) {
            Result.Error(exception.toErrorEntity())
        }
    }

    override suspend fun update(
        id: String,
        name: String,
        userId: String,
        description: String,
        restaurantIds: List<String>,
        visibility: GuideVisibility,
    ): Result<Guide> {
        return try {
            val guide = guideApi.update(
                id = id,
                name = name,
                userId = userId,
                description = description,
                restaurantIds = restaurantIds,
                visibility = visibility.name,
            ).toGuide()
            Result.Success(guide)
        } catch (exception: Exception) {
            Result.Error(exception.toErrorEntity())
        }
    }

    override suspend fun findById(id: String): Result<Guide> {
        return try {
            val guide = guideApi.findById(id).toGuide()
            Result.Success(guide)

        } catch (exception: Exception) {
            Result.Error(exception.toErrorEntity())
        }
    }

    override suspend fun getPhotoPresignedUrl(
        guideId: String,
        fileName: String,
        mimeType: String,
    ): Result<PresignedUrlData> {
        return try {
            val response = guideApi.getGuidePhotoPresignedUrl(
                guideId = guideId,
                request = PresignedUrlRequest(
                    fileName = fileName,
                    mimeType = mimeType,
                    context = "guide",
                ),
            )
            Result.Success(
                PresignedUrlData(
                    uploadUrl = response.uploadUrl,
                    publicUrl = response.publicUrl,
                )
            )
        } catch (exception: Exception) {
            Result.Error(exception.toErrorEntity())
        }
    }

    override suspend fun addRestaurantGuide(
        guideId: String,
        userId: String,
        placeId: String?
    ): Result<Guide> {
        return try {
            val guide = guideApi.addRestaurantGuide(
                guideId = guideId,
                placeId = placeId
            ).toGuide()
            Result.Success(guide)
        } catch (exception: Exception) {
            Result.Error(exception.toErrorEntity())
        }
    }

    override suspend fun addPhoto(
        guideId: String,
        imageUrl: String
    ): Result<Boolean> {
        return try {
            guideApi.addPhoto(
                guideId = guideId,
                imageUrl = imageUrl
            )
            Result.Success(true)
        } catch (exception: Exception) {
            Result.Error(exception.toErrorEntity())
        }
    }

    override suspend fun deletePhoto(guideId: String): Result<Boolean> {
        return try {
            guideApi.deletePhoto(
                guideId = guideId,
            ).toGuide()
            Result.Success(true)
        } catch (exception: Exception) {
            Result.Error(exception.toErrorEntity())
        }
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