package pt.socialfood.core.data.mapper

import pt.socialfood.core.model.Search
import pt.socialfood.core.network.model.search.SearchResponse

fun SearchResponse.toSearchResults(): List<Search> = authors.items.map { Search.AuthorResult(it.toAuthor()) } +
    guides.items.map { Search.GuideResult(it.toGuide()) } +
    restaurants.items.map { Search.RestaurantResult(it.toRestaurant()) }
