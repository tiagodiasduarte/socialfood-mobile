package pt.socialfood.fakes

import pt.socialfood.data.api.GuidesApi
import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.author.AuthorResponse
import pt.socialfood.data.network.model.guide.GuideDetailResponse
import pt.socialfood.data.network.model.guide.GuideResponse
import pt.socialfood.data.network.model.photo.PresignedUrlRequest
import pt.socialfood.data.network.model.photo.PresignedUrlResponse
import pt.socialfood.domain.model.GuideVisibility

private val defaultFakeAuthor = AuthorResponse(id = "author-id", name = "Author Name", imageUrl = null)

private val defaultFakeGuideDetail =
    GuideDetailResponse(
        id = "guide-id",
        name = "Guide Name",
        description = "Guide Description",
        visibility = GuideVisibility.PUBLIC,
        author = defaultFakeAuthor,
        restaurants = emptyList(),
        imageUrl = null,
    )

private val defaultFakeGuideResponse =
    GuideResponse(
        id = "guide-id",
        name = "Guide Name",
        description = "Guide Description",
        visibility = GuideVisibility.PUBLIC,
        author = defaultFakeAuthor,
        numberOfRestaurants = 0,
        imageUrl = null,
    )

class FakeGuidesApi(
    private val shouldThrow: Boolean = false,
    private val items: List<GuideResponse> = listOf(defaultFakeGuideResponse),
    private val total: Int = 25,
) : GuidesApi {
    var findGuidesCallCount: Int = 0
        private set
    var lastFindGuidesPage: Int? = null
        private set
    var lastFindGuidesUserId: String? = null
        private set

    override suspend fun create(
        name: String,
        description: String,
        userId: String,
    ): GuideDetailResponse {
        if (shouldThrow) throw RuntimeException("test error")
        return defaultFakeGuideDetail
    }

    override suspend fun delete(id: String) {
        if (shouldThrow) throw RuntimeException("test error")
    }

    override suspend fun findAll(): List<GuideResponse> {
        if (shouldThrow) throw RuntimeException("test error")
        return listOf(defaultFakeGuideResponse)
    }

    override suspend fun findGuides(
        page: Int,
        limit: Int,
        query: String?,
        userId: String?,
    ): PagedResponse<GuideResponse> {
        if (shouldThrow) throw RuntimeException("test error")
        findGuidesCallCount++
        lastFindGuidesPage = page
        lastFindGuidesUserId = userId
        return PagedResponse(
            items = items,
            page = page,
            limit = limit,
            total = total,
        )
    }

    override suspend fun findById(id: String): GuideDetailResponse {
        if (shouldThrow) throw RuntimeException("test error")
        return defaultFakeGuideDetail
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
        return defaultFakeGuideDetail
    }

    override suspend fun addRestaurantGuide(
        guideId: String,
        placeId: String?,
    ): GuideDetailResponse {
        if (shouldThrow) throw RuntimeException("test error")
        return defaultFakeGuideDetail
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

    override suspend fun addPhoto(
        guideId: String,
        imageUrl: String,
    ): GuideDetailResponse {
        if (shouldThrow) throw RuntimeException("test error")
        return defaultFakeGuideDetail
    }

    override suspend fun deletePhoto(guideId: String): GuideDetailResponse {
        if (shouldThrow) throw RuntimeException("test error")
        return defaultFakeGuideDetail
    }
}
