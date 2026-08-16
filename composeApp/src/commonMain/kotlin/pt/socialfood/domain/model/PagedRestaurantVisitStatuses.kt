package pt.socialfood.domain.model

data class PagedRestaurantVisitStatuses(
    val visits: List<RestaurantVisitStatus>,
    val page: Int,
    val total: Int,
    val hasMore: Boolean,
)
