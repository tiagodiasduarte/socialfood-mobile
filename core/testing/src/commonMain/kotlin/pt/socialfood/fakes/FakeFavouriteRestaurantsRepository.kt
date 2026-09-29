package pt.socialfood.fakes

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import pt.socialfood.core.Result
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.repository.FavouriteRestaurantsRepository

class FakeFavouriteRestaurantsRepository(
    private val markResult: Result<Unit> = Result.Success(Unit),
    private val unmarkResult: Result<Unit> = Result.Success(Unit),
    private val isFavouriteResult: Result<Boolean> = Result.Success(false),
    private val syncResult: Result<Unit> = Result.Success(Unit),
    private val pagingFlow: () -> Flow<PagingData<Restaurant>> = { flowOf(PagingData.empty()) },
) : FavouriteRestaurantsRepository {
    var markInvokeCount: Int = 0
        private set
    var lastMarkedRestaurant: Restaurant? = null
        private set

    var unmarkInvokeCount: Int = 0
        private set
    var lastUnmarkedRestaurantId: String? = null
        private set

    var lastIsFavouriteRestaurantId: String? = null
        private set

    var pagingInvokeCount: Int = 0
        private set

    var syncInvokeCount: Int = 0
        private set

    override suspend fun markFavourite(restaurant: Restaurant): Result<Unit> {
        markInvokeCount++
        lastMarkedRestaurant = restaurant
        return markResult
    }

    override suspend fun unmarkFavourite(restaurantId: String): Result<Unit> {
        unmarkInvokeCount++
        lastUnmarkedRestaurantId = restaurantId
        return unmarkResult
    }

    override fun getFavouritesPagingFlow(): Flow<PagingData<Restaurant>> {
        pagingInvokeCount++
        return pagingFlow()
    }

    override suspend fun isFavourite(restaurantId: String): Result<Boolean> {
        lastIsFavouriteRestaurantId = restaurantId
        return isFavouriteResult
    }

    override suspend fun syncFavourites(): Result<Unit> {
        syncInvokeCount++
        return syncResult
    }
}
