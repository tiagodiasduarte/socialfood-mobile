package pt.socialfood.domain.repository

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.domain.model.PagedGuides
import pt.socialfood.domain.model.PresignedUrlData
import pt.socialfood.domain.model.Restaurant


interface GuidesRepository {


    suspend fun delete(id: String): Result<Boolean>

    suspend fun create(name: String, description: String, userId: String): Result<Guide>

    suspend fun update(
        id: String,
        name: String,
        userId: String,
        description: String,
        restaurantIds: List<String>,
        visibility: GuideVisibility,
    ): Result<Guide>

    suspend fun findGuides(): Result<List<Guide>>

    suspend fun findGuidesPaged(page: Int, limit: Int, query: String? = null): Result<PagedGuides>

    suspend fun findById(id: String): Result<Guide>

    suspend fun addRestaurantGuide(
        guideId: String,
        userId: String,
        placeId: String?,
    ): Result<Guide>

    suspend fun addPhoto(guideId: String, imageUrl: String): Result<Boolean>

    suspend fun deletePhoto(guideId: String): Result<Boolean>

    suspend fun getPhotoPresignedUrl(
        guideId: String,
        fileName: String,
        mimeType: String,
    ): Result<PresignedUrlData>
}