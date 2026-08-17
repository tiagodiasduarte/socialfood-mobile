package pt.socialfood.random

import pt.socialfood.domain.model.AuthorDetail
import kotlin.random.Random

fun Random.nextAuthorDetail(
    id: String = nextString(),
    name: String = nextString(),
    username: String = nextString(),
    imageUrl: String? = nextNullable { nextUrl() },
    guidesCount: Int = nextInt(0, 100),
    followersCount: Int = nextInt(0, 10_000),
    followingCount: Int = nextInt(0, 10_000),
    facebookUrl: String? = nextNullable { nextUrl() },
    instagramUrl: String? = nextNullable { nextUrl() },
    youtubeUrl: String? = nextNullable { nextUrl() },
    guides: List<AuthorDetail.Guide> = nextList { nextAuthorDetailGuide() },
) = AuthorDetail(
    id = id,
    name = name,
    username = username,
    imageUrl = imageUrl,
    guidesCount = guidesCount,
    followersCount = followersCount,
    followingCount = followingCount,
    facebookUrl = facebookUrl,
    instagramUrl = instagramUrl,
    youtubeUrl = youtubeUrl,
    guides = guides,
)

fun Random.nextAuthorDetailGuide(
    id: String = nextString(),
    imageUrl: String? = nextNullable { nextUrl() },
    name: String = nextString(),
    description: String = nextString(),
    numberOfRestaurant: Int = nextInt(0, 50),
) = AuthorDetail.Guide(
    id = id,
    imageUrl = imageUrl,
    name = name,
    description = description,
    numberOfRestaurant = numberOfRestaurant,
)
