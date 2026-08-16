package pt.socialfood.domain.model

data class PagedRestaurantVisitStatus(
    val visits: List<RestaurantVisitStatus>,
    val page: Int,
    val total: Int,
    val hasMore: Boolean,
)
