package pt.socialfood.core.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import pt.socialfood.core.common.Result
import pt.socialfood.core.model.Restaurant

interface FavouriteRestaurantsRepository {
    suspend fun markFavourite(restaurant: Restaurant): Result<Unit>

    suspend fun unmarkFavourite(restaurantId: String): Result<Unit>

    /**
     * Room-backed, refresh-on-fetch paging stream for the favourite restaurants list. Returns
     * `Flow<PagingData<Restaurant>>` rather than a one-shot `Result` because Paging's error surface
     * is `LoadState`/`RemoteMediator.MediatorResult`, not `Result`; see
     * `FavouriteRestaurantRemoteMediator` for the sync logic.
     */
    fun getFavouritesPagingFlow(): Flow<PagingData<Restaurant>>

    suspend fun isFavourite(restaurantId: String): Result<Boolean>

    suspend fun syncFavourites(): Result<Unit>
}
