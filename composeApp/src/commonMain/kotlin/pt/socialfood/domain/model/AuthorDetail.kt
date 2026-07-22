package pt.socialfood.domain.model

data class AuthorDetail(
    val id: String,
    val name: String,
    val bio: String? = null,
    val imageUrl: String? = null,
    val guidesCount: Int = 0,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val guides: List<Guide> = emptyList(),
){
    data class Guide(
        val id: String,
        val imageUrl: String?,
        val name: String,
        val description: String,
        val numberOfRestaurant: Int,
    )
}
