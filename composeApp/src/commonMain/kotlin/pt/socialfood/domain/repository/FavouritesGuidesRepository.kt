package pt.socialfood.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import pt.socialfood.core.Result
import pt.socialfood.domain.model.Guide

interface FavouritesGuidesRepository {
    suspend fun markFavourite(guide: Guide): Result<Unit>

    suspend fun unmarkFavourite(guideId: String): Result<Unit>

    /**
     * Room-backed, refresh-on-fetch paging stream for the favourite guides list. Returns
     * `Flow<PagingData<Guide>>` rather than a one-shot `Result` because Paging's error surface is
     * `LoadState`/`RemoteMediator.MediatorResult`, not `Result`; see `FavouriteGuideRemoteMediator`
     * for the sync logic.
     */
    fun getFavouritesPagingFlow(): Flow<PagingData<Guide>>

    suspend fun isFavourite(guideId: String): Result<Boolean>

    // Local-cache-only reactive stream, same intentional exception to the Result<T> convention
    // as GuidesRepository.getGuidesPagingFlow — there's no network call or error surface to wrap.
    fun observeFavouriteGuideIds(): Flow<Set<String>>

    suspend fun syncFavourites(): Result<Unit>
}
