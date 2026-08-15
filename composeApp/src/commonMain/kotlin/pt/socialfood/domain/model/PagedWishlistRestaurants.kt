package pt.socialfood.domain.model

data class PagedWishlistRestaurants(
    val wishlist: List<WishlistRestaurant>,
    val page: Int,
    val total: Int,
    val hasMore: Boolean,
)
