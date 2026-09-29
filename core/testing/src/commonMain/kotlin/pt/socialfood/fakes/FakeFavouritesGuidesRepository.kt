package pt.socialfood.fakes

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import pt.socialfood.core.Result
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.repository.FavouritesGuidesRepository

class FakeFavouritesGuidesRepository(
    private val markResult: Result<Unit> = Result.Success(Unit),
    private val unmarkResult: Result<Unit> = Result.Success(Unit),
    private val isFavouriteResult: Result<Boolean> = Result.Success(false),
    private val syncResult: Result<Unit> = Result.Success(Unit),
    private val pagingFlow: () -> Flow<PagingData<Guide>> = { flowOf(PagingData.empty()) },
    favouriteGuideIds: Set<String> = emptySet(),
) : FavouritesGuidesRepository {
    private val favouriteGuideIdsFlow = MutableStateFlow(favouriteGuideIds)

    fun emitFavouriteGuideIds(ids: Set<String>) {
        favouriteGuideIdsFlow.value = ids
    }

    var markInvokeCount: Int = 0
        private set
    var lastMarkedGuide: Guide? = null
        private set

    var unmarkInvokeCount: Int = 0
        private set
    var lastUnmarkedGuideId: String? = null
        private set

    var lastIsFavouriteGuideId: String? = null
        private set

    var pagingInvokeCount: Int = 0
        private set

    var syncInvokeCount: Int = 0
        private set

    override suspend fun mark(guide: Guide): Result<Unit> {
        markInvokeCount++
        lastMarkedGuide = guide
        return markResult
    }

    override suspend fun unmark(guideId: String): Result<Unit> {
        unmarkInvokeCount++
        lastUnmarkedGuideId = guideId
        return unmarkResult
    }

    override fun getFavouritesPagingFlow(): Flow<PagingData<Guide>> {
        pagingInvokeCount++
        return pagingFlow()
    }

    override suspend fun isFavourite(guideId: String): Result<Boolean> {
        lastIsFavouriteGuideId = guideId
        return isFavouriteResult
    }

    override fun observeFavouriteGuideIds(): Flow<Set<String>> = favouriteGuideIdsFlow

    override suspend fun sync(): Result<Unit> {
        syncInvokeCount++
        return syncResult
    }
}
