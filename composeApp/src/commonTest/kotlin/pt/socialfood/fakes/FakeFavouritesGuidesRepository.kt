package pt.socialfood.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import pt.socialfood.core.Result
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.PagedFavouriteGuides
import pt.socialfood.domain.repository.FavouritesGuidesRepository

class FakeFavouritesGuidesRepository(
    private val markResult: Result<Unit> = Result.Success(Unit),
    private val unmarkResult: Result<Unit> = Result.Success(Unit),
    private val pagedResult: Result<PagedFavouriteGuides> = Result.Success(
        PagedFavouriteGuides(favourites = emptyList(), page = 1, total = 0, hasMore = false),
    ),
    private val isFavouriteResult: Result<Boolean> = Result.Success(false),
    private val favouriteGuideIds: Flow<Set<String>> = flowOf(emptySet()),
    private val syncResult: Result<Unit> = Result.Success(Unit),
) : FavouritesGuidesRepository {

    var lastMarkedGuide: Guide? = null
        private set

    var lastUnmarkedGuideId: String? = null
        private set

    var lastIsFavouriteGuideId: String? = null
        private set

    override suspend fun markFavourite(guide: Guide): Result<Unit> {
        lastMarkedGuide = guide
        return markResult
    }

    override suspend fun unmarkFavourite(guideId: String): Result<Unit> {
        lastUnmarkedGuideId = guideId
        return unmarkResult
    }

    override suspend fun getFavouritesPaged(page: Int, limit: Int): Result<PagedFavouriteGuides> = pagedResult

    override suspend fun isFavourite(guideId: String): Result<Boolean> {
        lastIsFavouriteGuideId = guideId
        return isFavouriteResult
    }

    override fun observeFavouriteGuideIds(): Flow<Set<String>> = favouriteGuideIds

    override suspend fun syncFavourites(): Result<Unit> = syncResult
}
