package pt.socialfood.random

import pt.socialfood.domain.model.AuthorDetail

@Suppress("LongParameterList")
fun randomAuthorDetail(
    id: String = randomString(),
    name: String = randomString(),
    username: String = randomString(),
    imageUrl: String? = randomNullable { randomUrl() },
    guidesCount: Int = randomInt(0, 100),
    followersCount: Int = randomInt(0, 10_000),
    followingCount: Int = randomInt(0, 10_000),
    facebookUrl: String? = randomNullable { randomUrl() },
    instagramUrl: String? = randomNullable { randomUrl() },
    youtubeUrl: String? = randomNullable { randomUrl() },
    guides: List<AuthorDetail.Guide> = randomList { randomAuthorDetailGuide() },
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

fun randomAuthorDetailGuide(
    id: String = randomString(),
    imageUrl: String? = randomNullable { randomUrl() },
    name: String = randomString(),
    description: String = randomString(),
    numberOfRestaurant: Int = randomInt(0, 50),
) = AuthorDetail.Guide(
    id = id,
    imageUrl = imageUrl,
    name = name,
    description = description,
    numberOfRestaurant = numberOfRestaurant,
)
