package pt.socialfood.domain.model

data class PagedRestaurantVisits(
    val visits: List<RestaurantVisit>,
    val page: Int,
    val total: Int,
    val hasMore: Boolean,
)
