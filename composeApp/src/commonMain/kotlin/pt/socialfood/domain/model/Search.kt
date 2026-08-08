package pt.socialfood.domain.model

data class Search(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String? = null,
    val type: SearchResultType,
)

enum class SearchResultType { AUTHOR, GUIDE, RESTAURANT }
