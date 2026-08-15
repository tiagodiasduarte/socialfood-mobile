package pt.socialfood.random

import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.domain.model.Restaurant
import kotlin.random.Random

fun Random.nextGuide(
    id: String = nextString(),
    name: String = nextString(),
    description: String = nextString(),
    visibility: GuideVisibility = nextEnum(),
    author: Author = nextAuthor(),
    numberOfRestaurant: Int = nextInt(0, 50),
    restaurants: List<Restaurant> = emptyList(),
    imageUrl: String? = nextNullable { nextUrl() },
) = Guide(
    id = id,
    name = name,
    description = description,
    visibility = visibility,
    author = author,
    numberOfRestaurant = numberOfRestaurant,
    restaurants = restaurants,
    imageUrl = imageUrl,
)
