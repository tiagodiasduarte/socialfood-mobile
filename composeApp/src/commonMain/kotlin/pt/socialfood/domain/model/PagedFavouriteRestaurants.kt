package pt.socialfood.domain.model

data class PagedFavouriteRestaurants(
    val favourites: List<FavouriteRestaurant>,
    val page: Int,
    val total: Int,
    val hasMore: Boolean,
)
