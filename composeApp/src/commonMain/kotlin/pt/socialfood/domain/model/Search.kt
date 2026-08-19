package pt.socialfood.domain.model

sealed interface Search {
    val id: String

    data class AuthorResult(val author: Author) : Search {
        override val id: String = author.id
    }

    data class GuideResult(val guide: Guide) : Search {
        override val id: String = guide.id
    }

    data class RestaurantResult(val restaurant: Restaurant) : Search {
        override val id: String = restaurant.id
    }
}
