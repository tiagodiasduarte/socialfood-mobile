package pt.socialfood.fakes

import kotlinx.io.IOException
import pt.socialfood.data.api.FavouritesGuidesApi
import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.author.AuthorResponse
import pt.socialfood.data.network.model.favourite.FavouriteSyncResponse
import pt.socialfood.data.network.model.guide.GuideResponse
import pt.socialfood.domain.model.GuideVisibility

class FakeFavouritesGuidesApi(private val shouldThrow: Boolean = false) : FavouritesGuidesApi {

    private val fakeAuthor = AuthorResponse(
        id = "author-id",
        name = "Author Name",
        username = "authorname",
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

    var lastMarkedGuideId: String? = null
        private set

    var lastUnmarkedGuideId: String? = null
        private set

    var findFavouriteGuidesCallCount: Int = 0
        private set

    var lastFindFavouriteGuidesPage: Int? = null
        private set

    var fakeFavouriteGuides = PagedResponse(
        items = listOf(fakeGuideResponse),
        page = 1,
        limit = 10,
        total = 1,
    )

    var fakeSyncResponse = FavouriteSyncResponse(
        added = listOf(fakeGuideResponse),
        removedIds = emptyList(),
        syncedAt = "2026-08-01T10:30:00Z",
    )

    override suspend fun mark(guideId: String) {
        if (shouldThrow) throw IOException("test error")
        lastMarkedGuideId = guideId
    }

    override suspend fun unmark(guideId: String) {
        if (shouldThrow) throw IOException("test error")
        lastUnmarkedGuideId = guideId
    }

    override suspend fun find(page: Int, limit: Int): PagedResponse<GuideResponse> {
        if (shouldThrow) throw IOException("test error")
        findFavouriteGuidesCallCount++
        lastFindFavouriteGuidesPage = page
        return fakeFavouriteGuides
    }

    override suspend fun sync(since: String?): FavouriteSyncResponse<GuideResponse> {
        if (shouldThrow) throw IOException("test error")
        return fakeSyncResponse
    }
}
