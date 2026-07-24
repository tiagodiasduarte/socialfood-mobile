package pt.socialfood.fakes

import pt.socialfood.data.GuidesApi
import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.author.AuthorResponse
import pt.socialfood.data.network.model.guide.GuideDetailResponse
import pt.socialfood.data.network.model.guide.GuideResponse
import pt.socialfood.data.network.model.photo.PresignedUrlRequest
import pt.socialfood.data.network.model.photo.PresignedUrlResponse
import pt.socialfood.domain.model.GuideVisibility

class FakeGuidesApi(private val shouldThrow: Boolean = false) : GuidesApi {

    private val fakeAuthor = AuthorResponse(id = "author-id", name = "Author Name", imageUrl = null)

    private val fakeGuideDetail = GuideDetailResponse(
        id = "guide-id",
        name = "Guide Name",
        description = "Guide Description",
        visibility = GuideVisibility.PUBLIC,
        author = fakeAuthor,
        restaurants = emptyList(),
        imageUrl = null,
    )

    private val fakeGuideResponse = GuideResponse(
        id = "guide-id",
        name = "Guide Name",
        description = "Guide Description",
        visibility = GuideVisibility.PUBLIC,
        author = fakeAuthor,
        numberOfRestaurants = 0,
        imageUrl = null,
    )

    override suspend fun create(name: String, description: String, userId: String): GuideDetailResponse {
        if (shouldThrow) throw RuntimeException("test error")
        return fakeGuideDetail
    }

    override suspend fun delete(id: String) {
        if (shouldThrow) throw RuntimeException("test error")
    }

    override suspend fun findAll(): List<GuideResponse> {
        if (shouldThrow) throw RuntimeException("test error")
        return listOf(fakeGuideResponse)
    }

    override suspend fun findGuides(page: Int, limit: Int, query: String?, userId: String?): PagedResponse<GuideResponse> {
        if (shouldThrow) throw RuntimeException("test error")
        return PagedResponse(
            items = listOf(fakeGuideResponse),
            page = page,
            limit = limit,
            total = 25,
        )
    }

    override suspend fun findById(id: String): GuideDetailResponse {
        if (shouldThrow) throw RuntimeException("test error")
        return fakeGuideDetail
    }

    override suspend fun update(
        id: String,
        name: String,
        userId: String,
        description: String,
        restaurantIds: List<String>,
        visibility: String,
    ): GuideDetailResponse {
        if (shouldThrow) throw RuntimeException("test error")
        return fakeGuideDetail
    }

    override suspend fun addRestaurantGuide(guideId: String, placeId: String?): GuideDetailResponse {
        if (shouldThrow) throw RuntimeException("test error")
        return fakeGuideDetail
    }

    override suspend fun getGuidePhotoPresignedUrl(
        guideId: String,
        request: PresignedUrlRequest,
    ): PresignedUrlResponse {
        if (shouldThrow) throw RuntimeException("test error")
        return PresignedUrlResponse(
            uploadUrl = "https://upload.example.com/photo",
            key = "photo-key",
            publicUrl = "https://public.example.com/photo",
        )
    }

    override suspend fun addPhoto(guideId: String, imageUrl: String): GuideDetailResponse {
        if (shouldThrow) throw RuntimeException("test error")
        return fakeGuideDetail
    }

    override suspend fun deletePhoto(guideId: String): GuideDetailResponse {
        if (shouldThrow) throw RuntimeException("test error")
        return fakeGuideDetail
    }
}
