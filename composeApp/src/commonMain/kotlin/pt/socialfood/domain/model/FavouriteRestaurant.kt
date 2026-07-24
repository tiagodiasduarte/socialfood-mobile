package pt.socialfood.domain.model

data class FavouriteRestaurant(
    val restaurant: Restaurant,
    val favouritedAt: Long,
)
