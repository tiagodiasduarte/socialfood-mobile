package pt.socialfood.random

import pt.socialfood.domain.model.Author
import kotlin.random.Random

fun Random.nextAuthor(
    id: String = nextString(),
    name: String = nextString(),
    username: String = nextString(),
    imageUrl: String? = nextNullable { nextUrl() },
) = Author(id = id, name = name, username = username, imageUrl = imageUrl)
