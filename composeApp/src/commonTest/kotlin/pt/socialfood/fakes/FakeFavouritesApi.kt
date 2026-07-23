package pt.socialfood.fakes

import pt.socialfood.data.FavouritesApi
import pt.socialfood.data.network.model.author.AuthorResponse
import pt.socialfood.data.network.model.favourite.FavouriteChangesResponse
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

    var fakeChangesResponse = FavouriteChangesResponse(
        added = listOf(fakeGuideResponse),
        removedGuideIds = emptyList(),
        nextCheckpoint = "checkpoint-1",
        hasMore = false,
    )

    override suspend fun markFavourite(guideId: String) {
        if (shouldThrow) throw RuntimeException("test error")
        lastMarkedGuideId = guideId
    }

    override suspend fun unmarkFavourite(guideId: String) {
        if (shouldThrow) throw RuntimeException("test error")
        lastUnmarkedGuideId = guideId
    }

    override suspend fun findFavouriteChanges(since: String?, limit: Int): FavouriteChangesResponse {
        if (shouldThrow) throw RuntimeException("test error")
        return fakeChangesResponse
    }
}
