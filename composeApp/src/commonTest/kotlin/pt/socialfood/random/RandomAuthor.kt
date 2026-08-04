package pt.socialfood.random

import pt.socialfood.domain.model.Author

fun randomAuthor(
    id: String = randomString(),
    name: String = randomString(),
    username: String = randomString(),
    imageUrl: String? = randomNullable { randomUrl() },
) = Author(id = id, name = name, username = username, imageUrl = imageUrl)
