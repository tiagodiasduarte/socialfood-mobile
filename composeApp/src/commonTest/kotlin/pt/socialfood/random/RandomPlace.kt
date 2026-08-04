package pt.socialfood.random

import pt.socialfood.domain.model.Place

fun randomPlace(
    id: String = randomString(),
    name: String = randomString(),
    address: String = randomString(12),
    imageUrl: String? = randomNullable { randomUrl() },
) = Place(id = id, name = name, address = address, imageUrl = imageUrl)
