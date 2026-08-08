package pt.socialfood.random

import pt.socialfood.domain.model.Search
import pt.socialfood.domain.model.SearchResultType
import kotlin.random.Random

fun Random.nextSearch(
    id: String = nextString(),
    name: String = nextString(),
    description: String = nextString(),
    imageUrl: String? = nextNullable { nextUrl() },
    type: SearchResultType = nextEnum(),
) = Search(id = id, name = name, description = description, imageUrl = imageUrl, type = type)
