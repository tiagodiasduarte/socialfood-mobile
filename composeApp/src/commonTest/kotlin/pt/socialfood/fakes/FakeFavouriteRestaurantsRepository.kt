package pt.socialfood.fakes

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import pt.socialfood.core.Result
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.repository.FavouriteRestaurantsRepository

class FakeFavouriteRestaurantsRepository(
    private val markResult: Result<Unit> = Result.Success(Unit),
    private val unmarkResult: Result<Unit> = Result.Success(Unit),
    private val pagingFlow: Flow<PagingData<Restaurant>> = emptyFlow(),
    private val isFavouriteResult: Result<Boolean> = Result.Success(false),
    private val syncResult: Result<Unit> = Result.Success(Unit),
) : FavouriteRestaurantsRepository {

    var lastMarkedRestaurant: Restaurant? = null
        private set

    var lastUnmarkedRestaurantId: String? = null
        private set

    var lastIsFavouriteRestaurantId: String? = null
        private set

    override suspend fun markFavourite(restaurant: Restaurant): Result<Unit> {
        lastMarkedRestaurant = restaurant
        return markResult
    }

    override suspend fun unmarkFavourite(restaurantId: String): Result<Unit> {
        lastUnmarkedRestaurantId = restaurantId
        return unmarkResult
    }

    override fun getFavouritesPagingFlow(): Flow<PagingData<Restaurant>> = pagingFlow

    override suspend fun isFavourite(restaurantId: String): Result<Boolean> {
        lastIsFavouriteRestaurantId = restaurantId
        return isFavouriteResult
    }

    override suspend fun syncFavourites(): Result<Unit> = syncResult
}
