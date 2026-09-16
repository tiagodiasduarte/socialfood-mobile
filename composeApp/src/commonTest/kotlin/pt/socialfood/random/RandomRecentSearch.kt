package pt.socialfood.random

import pt.socialfood.domain.model.RecentSearch
import pt.socialfood.domain.model.RecentSearchType
import kotlin.random.Random

fun Random.nextRecentSearch(
    id: String = nextString(),
    type: RecentSearchType = RecentSearchType.entries.random(this),
    title: String = nextString(),
) = RecentSearch(id = id, type = type, title = title)
