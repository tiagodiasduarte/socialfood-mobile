package pt.socialfood.domain.model

data class PagedUsers(
    val users: List<User>,
    val page: Int,
    val total: Int,
    val hasMore: Boolean,
)
