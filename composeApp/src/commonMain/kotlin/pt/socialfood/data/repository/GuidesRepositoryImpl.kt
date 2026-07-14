package pt.socialfood.data.repository

import pt.socialfood.core.Result
import pt.socialfood.data.GuidesApi
import pt.socialfood.data.network.extensions.toErrorEntity
import pt.socialfood.data.network.model.photo.PresignedUrlRequest
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.domain.model.PagedGuides
import pt.socialfood.domain.model.PresignedUrlData
import pt.socialfood.domain.repository.GuidesRepository
import pt.socialfood.mapper.toGuide

class GuidesRepositoryImpl(
    private val guideApi: GuidesApi
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

    override suspend fun findGuidesPaged(page: Int, limit: Int, query: String?): Result<PagedGuides> {
        return try {
            val response = guideApi.findGuides(page = page, limit = limit, query = query)
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
}