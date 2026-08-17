package pt.socialfood.random

import pt.socialfood.domain.model.PagedUsers
import pt.socialfood.domain.model.User
import kotlin.random.Random

fun Random.nextPagedUsers(
    users: List<User> = nextList { nextUser() },
    page: Int = nextInt(1, 10),
    total: Int = users.size,
    hasMore: Boolean = nextBoolean(),
) = PagedUsers(users = users, page = page, total = total, hasMore = hasMore)
