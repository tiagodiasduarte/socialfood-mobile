package pt.socialfood.random

import pt.socialfood.domain.model.PagedUsers
import pt.socialfood.domain.model.User

fun randomPagedUsers(
    users: List<User> = randomList { randomUser() },
    page: Int = randomInt(1, 10),
    total: Int = users.size,
    hasMore: Boolean = randomBoolean(),
) = PagedUsers(users = users, page = page, total = total, hasMore = hasMore)
