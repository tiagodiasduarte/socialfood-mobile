package pt.socialfood.mapper

import pt.socialfood.data.network.model.search.SearchResponse
import pt.socialfood.domain.model.Search

fun SearchResponse.toSearchResults(): List<Search> = authors.items.map { Search.AuthorResult(it.toAuthor()) } +
    guides.items.map { Search.GuideResult(it.toGuide()) } +
    restaurants.items.map { Search.RestaurantResult(it.toRestaurant()) }
