package pt.socialfood.domain.repository

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedFavouriteRestaurants
import pt.socialfood.domain.model.Restaurant

interface FavouriteRestaurantsRepository {
    suspend fun markFavourite(restaurant: Restaurant): Result<Unit>

    suspend fun unmarkFavourite(restaurantId: String): Result<Unit>

    suspend fun getFavouritesPaged(page: Int, limit: Int): Result<PagedFavouriteRestaurants>

    suspend fun isFavourite(restaurantId: String): Result<Boolean>

    suspend fun syncFavourites(): Result<Unit>
}
