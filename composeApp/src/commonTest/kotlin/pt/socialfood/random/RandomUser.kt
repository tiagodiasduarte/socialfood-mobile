package pt.socialfood.random

import pt.socialfood.domain.model.User
import pt.socialfood.domain.model.UserRole

@Suppress("LongParameterList")
fun randomUser(
    id: String = randomId("user"),
    email: String = randomEmail(),
    name: String = randomString(),
    username: String = randomString(),
    imageUrl: String? = randomNullable { randomUrl() },
    role: UserRole = randomEnum(),
    city: String? = randomNullable { randomString() },
    country: String? = randomNullable { randomString() },
    facebookUrl: String? = randomNullable { randomUrl() },
    instagramUrl: String? = randomNullable { randomUrl() },
    youtubeUrl: String? = randomNullable { randomUrl() },
    isVerified: Boolean = randomBoolean(),
) = User(
    id = id,
    email = email,
    name = name,
    username = username,
    imageUrl = imageUrl,
    role = role,
    city = city,
    country = country,
    facebookUrl = facebookUrl,
    instagramUrl = instagramUrl,
    youtubeUrl = youtubeUrl,
    isVerified = isVerified,
)
