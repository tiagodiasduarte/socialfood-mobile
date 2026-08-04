package pt.socialfood.random

import pt.socialfood.domain.model.FavouriteGuide
import pt.socialfood.domain.model.Guide

fun randomFavouriteGuide(guide: Guide = randomGuide(), favouritedAt: Long = randomLong()) =
    FavouriteGuide(guide = guide, favouritedAt = favouritedAt)
