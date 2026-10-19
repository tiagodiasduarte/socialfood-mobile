package pt.socialfood.core.model

data class PagedUsers(val users: List<User>, val page: Int, val total: Int, val hasMore: Boolean)
