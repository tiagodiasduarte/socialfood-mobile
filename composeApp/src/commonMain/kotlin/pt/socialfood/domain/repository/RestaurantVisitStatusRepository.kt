package pt.socialfood.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedRestaurantVisitStatus
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.RestaurantVisitStatus
import pt.socialfood.domain.model.VisitStatus

interface RestaurantVisitStatusRepository {
    suspend fun mark(restaurant: Restaurant, status: VisitStatus): Result<Unit>

    suspend fun unmark(restaurantId: String, status: VisitStatus): Result<Unit>

    suspend fun getStatus(restaurantId: String): Result<VisitStatus?>

    /**
     * @deprecated in favor of [getPagingFlow]. Kept temporarily so the not-yet-migrated
     * Wishlist/Visited screens keep working; remove once both are on Paging 3.
     */
    suspend fun getPaged(status: VisitStatus, page: Int, limit: Int): Result<PagedRestaurantVisitStatus>

    /**
     * Room-backed, refresh-on-fetch paging stream for the wishlist/visited list, scoped to
     * [status]. Returns `Flow<PagingData<RestaurantVisitStatus>>` rather than a one-shot `Result`
     * because Paging's error surface is `LoadState`/`RemoteMediator.MediatorResult`, not `Result`;
     * see `RestaurantVisitStatusRemoteMediator` for the sync logic.
     */
    fun getPagingFlow(status: VisitStatus): Flow<PagingData<RestaurantVisitStatus>>

    suspend fun sync(): Result<Unit>
}
