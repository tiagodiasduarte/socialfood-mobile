package pt.socialfood.fakes

import kotlinx.io.IOException
import pt.socialfood.data.api.FavouritesApi
import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.author.AuthorResponse
import pt.socialfood.data.network.model.favourite.FavouriteSyncResponse
import pt.socialfood.data.network.model.guide.GuideResponse
import pt.socialfood.domain.model.GuideVisibility

class FakeFavouritesApi(private val shouldThrow: Boolean = false) : FavouritesApi {

    private val fakeAuthor = AuthorResponse(id = "author-id", name = "Author Name", imageUrl = null)

    private val fakeGuideResponse = GuideResponse(
        id = "guide-id",
        name = "Guide Name",
        description = "Guide Description",
        visibility = GuideVisibility.PUBLIC,
        author = fakeAuthor,
        numberOfRestaurants = 0,
        imageUrl = null,
    )

    var lastMarkedGuideId: String? = null
        private set

    var lastUnmarkedGuideId: String? = null
        private set

    var fakeFavouriteGuides = PagedResponse(
        items = listOf(fakeGuideResponse),
        page = 1,
        limit = 10,
        total = 1,
    )

    var fakeSyncResponse = FavouriteSyncResponse(
        addedIds = listOf(fakeGuideResponse.id),
        removedIds = emptyList(),
        lastUpdate = "2026-08-01T10:30:00Z",
    )

    override suspend fun markFavourite(guideId: String) {
        if (shouldThrow) throw IOException("test error")
        lastMarkedGuideId = guideId
    }

    override suspend fun unmarkFavourite(guideId: String) {
        if (shouldThrow) throw IOException("test error")
        lastUnmarkedGuideId = guideId
    }

    override suspend fun findFavouriteGuides(page: Int, limit: Int): PagedResponse<GuideResponse> {
        if (shouldThrow) throw IOException("test error")
        return fakeFavouriteGuides
    }

    override suspend fun syncFavouriteGuides(since: String?): FavouriteSyncResponse {
        if (shouldThrow) throw IOException("test error")
        return fakeSyncResponse
    }
}
