package pt.socialfood.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.guide.AddRestaurantGuideRequest
import pt.socialfood.data.network.model.guide.CreateGuideRequest
import pt.socialfood.data.network.model.guide.GuideDetailResponse
import pt.socialfood.data.network.model.guide.GuideResponse
import pt.socialfood.data.network.model.guide.UpdateGuidePhotoRequest
import pt.socialfood.data.network.model.guide.UpdateGuideRequest
import pt.socialfood.data.network.model.photo.PresignedUrlRequest
import pt.socialfood.data.network.model.photo.PresignedUrlResponse

class GuidesApiImpl(
    private val client: HttpClient
) : GuidesApi {

    override suspend fun create(
        name: String,
        description: String,
        userId: String,
    ): GuideDetailResponse =
        client.post("guides") {
            contentType(ContentType.Application.Json)
            setBody(
                CreateGuideRequest(
                    name = name,
                    description = description,
                    userId = userId,
                )
            )
        }.body()

    override suspend fun delete(id: String) {
        client.delete("guides/$id")
    }

    override suspend fun findAll(): List<GuideResponse> =
        client.get("guides").body()

    override suspend fun findGuides(page: Int, limit: Int, query: String?, userId: String?): PagedResponse<GuideResponse> =
        client.get("guides") {
            parameter("page", page)
            parameter("limit", limit)
            if (!query.isNullOrBlank()) parameter("search", query)
            if (userId != null) parameter("userId", userId)
        }.body()

    override suspend fun findById(id: String): GuideDetailResponse =
        client.get("guides/$id").body()

    override suspend fun update(
        id: String,
        name: String,
        userId: String,
        description: String,
        restaurantIds: List<String>,
        visibility: String,
    ): GuideDetailResponse =
        client.put("guides/$id") {
            contentType(ContentType.Application.Json)
            setBody(
                UpdateGuideRequest(
                    name = name,
                    userId = userId,
                    description = description,
                    restaurantIds = restaurantIds,
                    visibility = visibility,
                )
            )
        }.body()

    override suspend fun addRestaurantGuide(
        guideId: String,
        placeId: String?,
    ): GuideDetailResponse =
        client.post("guides/$guideId/restaurant") {
            contentType(ContentType.Application.Json)
            setBody(
                AddRestaurantGuideRequest(
                    guideId = guideId,
                    placeId = placeId,
                )
            )
        }.body()

    override suspend fun getGuidePhotoPresignedUrl(
        guideId: String,
        request: PresignedUrlRequest,
    ): PresignedUrlResponse =
        client.post("guides/$guideId/photo/presigned-url") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    override suspend fun addPhoto(guideId: String, imageUrl: String): GuideDetailResponse =
        client.post("guides/$guideId/photo") {
            contentType(ContentType.Application.Json)
            setBody(UpdateGuidePhotoRequest(imageUrl = imageUrl))
        }.body()

    override suspend fun deletePhoto(guideId: String): GuideDetailResponse =
        client.delete("guides/$guideId/photo") {
            contentType(ContentType.Application.Json)
        }.body()
}
