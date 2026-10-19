package pt.socialfood.core.model

data class PagedRestaurants(val restaurants: List<Restaurant>, val page: Int, val total: Int, val hasMore: Boolean)
