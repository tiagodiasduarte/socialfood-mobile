package pt.socialfood.data.api

import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.guide.GuideDetailResponse
import pt.socialfood.data.network.model.guide.GuideResponse
import pt.socialfood.data.network.model.guide.JoinGuideRequest
import pt.socialfood.data.network.model.photo.PresignedUrlRequest
import pt.socialfood.data.network.model.photo.PresignedUrlResponse

@Suppress("TooManyFunctions")
interface GuidesApi {
    suspend fun addPhoto(guideId: String, imageUrl: String)

    suspend fun addRestaurantGuide(guideId: String, placeId: String?): GuideDetailResponse

    suspend fun create(name: String, description: String, userId: String): GuideDetailResponse

    suspend fun delete(id: String)

    suspend fun deletePhoto(guideId: String)

    suspend fun findAll(): List<GuideResponse>

    suspend fun findById(id: String): GuideDetailResponse

    suspend fun findGuideBySharedCode(code: String): GuideDetailResponse

    suspend fun findGuides(
        page: Int,
        limit: Int,
        query: String? = null,
        userId: String? = null,
    ): PagedResponse<GuideResponse>

    suspend fun findUserJoinedGuides(page: Int, limit: Int): PagedResponse<GuideResponse>

    suspend fun findUserGuides(page: Int, limit: Int): PagedResponse<GuideResponse>

    suspend fun getGuidePhotoPresignedUrl(guideId: String, request: PresignedUrlRequest): PresignedUrlResponse

    suspend fun joinGuide(guideId: String, request: JoinGuideRequest): GuideDetailResponse

    suspend fun leaveGuide(guideId: String)

    suspend fun update(
        id: String,
        name: String,
        userId: String,
        description: String,
        restaurantIds: List<String>,
        visibility: String,
    ): GuideDetailResponse
}
