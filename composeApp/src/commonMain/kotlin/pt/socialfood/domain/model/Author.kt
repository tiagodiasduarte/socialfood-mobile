package pt.socialfood.domain.model

data class Author(
    val id: String,
    val name: String,
    val imageUrl: String? = null,
    val isFollowing: Boolean = false,
)
