package pt.socialfood.data

import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.guide.GuideDetailResponse
import pt.socialfood.data.network.model.guide.GuideResponse
import pt.socialfood.data.network.model.photo.PresignedUrlRequest
import pt.socialfood.data.network.model.photo.PresignedUrlResponse

interface GuidesApi {
    suspend fun create(
        name: String,
        description: String,
        userId: String,
    ): GuideDetailResponse

    suspend fun delete(id: String)

    suspend fun findAll(): List<GuideResponse>

    suspend fun findGuides(page: Int, limit: Int, query: String? = null, userId: String? = null): PagedResponse<GuideResponse>

    suspend fun findById(id: String): GuideDetailResponse

    suspend fun update(
        id: String,
        name: String,
        userId: String,
        description: String,
        restaurantIds: List<String>,
        visibility: String,
    ): GuideDetailResponse

    suspend fun addRestaurantGuide(
        guideId: String,
        placeId: String?,
    ): GuideDetailResponse

    suspend fun getGuidePhotoPresignedUrl(
        guideId: String,
        request: PresignedUrlRequest,
    ): PresignedUrlResponse

    suspend fun addPhoto(guideId: String, imageUrl: String): GuideDetailResponse

    suspend fun deletePhoto(guideId: String): GuideDetailResponse
}
