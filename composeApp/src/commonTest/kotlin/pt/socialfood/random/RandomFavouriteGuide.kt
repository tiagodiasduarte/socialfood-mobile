package pt.socialfood.random

import pt.socialfood.domain.model.FavouriteGuide
import pt.socialfood.domain.model.Guide
import kotlin.random.Random

fun Random.nextFavouriteGuide(guide: Guide = nextGuide(), favouritedAt: Long = nextLong()) =
    FavouriteGuide(guide = guide, favouritedAt = favouritedAt)
