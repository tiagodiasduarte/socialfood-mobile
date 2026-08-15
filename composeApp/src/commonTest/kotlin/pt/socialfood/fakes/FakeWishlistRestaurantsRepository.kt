package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedWishlistRestaurants
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.repository.WishlistRestaurantsRepository

class FakeWishlistRestaurantsRepository(
    private val markResult: Result<Unit> = Result.Success(Unit),
    private val unmarkResult: Result<Unit> = Result.Success(Unit),
    private val pagedResult: Result<PagedWishlistRestaurants> = Result.Success(
        PagedWishlistRestaurants(wishlist = emptyList(), page = 1, total = 0, hasMore = false),
    ),
    private val syncResult: Result<Unit> = Result.Success(Unit),
) : WishlistRestaurantsRepository {

    var lastMarkedRestaurant: Restaurant? = null
        private set

    var lastUnmarkedRestaurantId: String? = null
        private set

    override suspend fun markWishlisted(restaurant: Restaurant): Result<Unit> {
        lastMarkedRestaurant = restaurant
        return markResult
    }

    override suspend fun unmarkWishlisted(restaurantId: String): Result<Unit> {
        lastUnmarkedRestaurantId = restaurantId
        return unmarkResult
    }

    override suspend fun getWishlistPaged(page: Int, limit: Int): Result<PagedWishlistRestaurants> = pagedResult

    override suspend fun syncWishlist(): Result<Unit> = syncResult
}
