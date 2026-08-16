package pt.socialfood.domain.model

data class PagedRestaurantVisits(
    val visits: List<RestaurantVisitStatus>,
    val page: Int,
    val total: Int,
    val hasMore: Boolean,
)
