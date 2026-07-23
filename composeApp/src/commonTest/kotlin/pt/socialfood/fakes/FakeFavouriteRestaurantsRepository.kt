package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedFavouriteRestaurants
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.repository.FavouriteRestaurantsRepository

class FakeFavouriteRestaurantsRepository(
    private val markResult: Result<Unit> = Result.Success(Unit),
    private val unmarkResult: Result<Unit> = Result.Success(Unit),
    private val pagedResult: Result<PagedFavouriteRestaurants> = Result.Success(
        PagedFavouriteRestaurants(favourites = emptyList(), page = 1, total = 0, hasMore = false)
    ),
    private val isFavouriteResult: Result<Boolean> = Result.Success(false),
    private val syncResult: Result<Unit> = Result.Success(Unit),
) : FavouriteRestaurantsRepository {

    var lastMarkedRestaurant: Restaurant? = null
        private set

    var lastUnmarkedRestaurantId: String? = null
        private set

    override suspend fun markFavourite(restaurant: Restaurant): Result<Unit> {
        lastMarkedRestaurant = restaurant
        return markResult
    }

    override suspend fun unmarkFavourite(restaurantId: String): Result<Unit> {
        lastUnmarkedRestaurantId = restaurantId
        return unmarkResult
    }

    override suspend fun getFavouritesPaged(page: Int, limit: Int): Result<PagedFavouriteRestaurants> = pagedResult

    override suspend fun isFavourite(restaurantId: String): Result<Boolean> = isFavouriteResult

    override suspend fun syncFavourites(): Result<Unit> = syncResult
}
