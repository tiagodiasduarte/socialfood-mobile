package pt.socialfood.random

import pt.socialfood.domain.model.Event
import kotlin.random.Random

fun Random.nextEvent(
    id: String = nextString(),
    name: String = nextString(),
    description: String = nextString(),
    imageUrl: String? = nextNullable { nextUrl() },
) = Event(id = id, name = name, description = description, imageUrl = imageUrl)
