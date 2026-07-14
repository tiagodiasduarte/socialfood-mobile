package pt.socialfood.domain.model

data class Guide(
    val id: String,
    val name: String,
    val description: String,
    val visibility: GuideVisibility,
    val author: Author,
    val numberOfRestaurant: Int,
    val restaurants: List<Restaurant> = emptyList(),
    val imageUrl: String? = null,
)

enum class GuideVisibility { PUBLIC, PRIVATE }