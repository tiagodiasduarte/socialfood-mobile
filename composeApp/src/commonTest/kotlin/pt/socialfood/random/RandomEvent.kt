package pt.socialfood.random

import pt.socialfood.domain.model.Event

fun randomEvent(
    id: String = randomString(),
    name: String = randomString(),
    description: String = randomString(),
    imageUrl: String? = randomNullable { randomUrl() },
) = Event(id = id, name = name, description = description, imageUrl = imageUrl)
