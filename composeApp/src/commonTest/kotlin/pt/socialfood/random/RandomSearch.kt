package pt.socialfood.random

import pt.socialfood.domain.model.Search
import kotlin.random.Random

fun Random.nextSearch(): Search = when (nextInt(3)) {
    0 -> Search.AuthorResult(nextAuthor())
    1 -> Search.GuideResult(nextGuide())
    else -> Search.RestaurantResult(nextRestaurant())
}
