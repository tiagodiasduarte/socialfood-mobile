package pt.socialfood.random

import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.domain.model.Restaurant

@Suppress("LongParameterList")
fun randomGuide(
    id: String = randomString(),
    name: String = randomString(),
    description: String = randomString(),
    visibility: GuideVisibility = randomEnum(),
    author: Author = randomAuthor(),
    numberOfRestaurant: Int = randomInt(0, 50),
    restaurants: List<Restaurant> = emptyList(),
    imageUrl: String? = randomNullable { randomUrl() },
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
