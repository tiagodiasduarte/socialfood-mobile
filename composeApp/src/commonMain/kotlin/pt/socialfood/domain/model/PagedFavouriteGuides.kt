package pt.socialfood.domain.model

data class PagedFavouriteGuides(
    val favourites: List<FavouriteGuide>,
    val page: Int,
    val total: Int,
    val hasMore: Boolean,
)
