package pt.socialfood.random

import pt.socialfood.domain.model.Place
import kotlin.random.Random

fun Random.nextPlace(
    id: String = nextString(),
    name: String = nextString(),
    address: String = nextString(12),
    imageUrl: String? = nextNullable { nextUrl() },
) = Place(id = id, name = name, address = address, imageUrl = imageUrl)
