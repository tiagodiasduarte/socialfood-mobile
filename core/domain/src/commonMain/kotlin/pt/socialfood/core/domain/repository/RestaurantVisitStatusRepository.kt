package pt.socialfood.core.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import pt.socialfood.core.common.Result
import pt.socialfood.core.model.Restaurant
import pt.socialfood.core.model.RestaurantVisitStatus
import pt.socialfood.core.model.VisitStatus

interface RestaurantVisitStatusRepository {
    suspend fun mark(restaurant: Restaurant, status: VisitStatus): Result<Unit>

    suspend fun unmark(restaurantId: String, status: VisitStatus): Result<Unit>

    suspend fun getStatus(restaurantId: String): Result<VisitStatus?>

    /**
     * Room-backed, refresh-on-fetch paging stream for the wishlist/visited list, scoped to
     * [status]. Returns `Flow<PagingData<RestaurantVisitStatus>>` rather than a one-shot `Result`
     * because Paging's error surface is `LoadState`/`RemoteMediator.MediatorResult`, not `Result`;
     * see `RestaurantVisitStatusRemoteMediator` for the sync logic.
     */
    fun getPagingFlow(status: VisitStatus): Flow<PagingData<RestaurantVisitStatus>>

    /** All restaurants for [status], unpaginated — used for the map view. */
    fun getAllFlow(status: VisitStatus): Flow<List<Restaurant>>

    suspend fun sync(): Result<Unit>
}
