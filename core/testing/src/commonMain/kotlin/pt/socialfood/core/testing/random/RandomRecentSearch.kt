package pt.socialfood.core.testing.random

import pt.socialfood.core.model.RecentSearch
import pt.socialfood.core.model.RecentSearchType
import kotlin.random.Random

fun Random.nextRecentSearch(
    id: String = nextString(),
    type: RecentSearchType = RecentSearchType.entries.random(this),
    title: String = nextString(),
) = RecentSearch(id = id, type = type, title = title)
