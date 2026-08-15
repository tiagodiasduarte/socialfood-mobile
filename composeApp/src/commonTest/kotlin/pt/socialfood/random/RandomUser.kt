package pt.socialfood.random

import pt.socialfood.domain.model.User
import pt.socialfood.domain.model.UserRole
import kotlin.random.Random

fun Random.nextUser(
    id: String = nextString(),
    email: String = nextEmail(),
    name: String = nextString(),
    username: String = nextString(),
    imageUrl: String? = nextNullable { nextUrl() },
    role: UserRole = nextEnum(),
    city: String? = nextNullable { nextString() },
    country: String? = nextNullable { nextString() },
    facebookUrl: String? = nextNullable { nextUrl() },
    instagramUrl: String? = nextNullable { nextUrl() },
    youtubeUrl: String? = nextNullable { nextUrl() },
    isVerified: Boolean = nextBoolean(),
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
