package pt.socialfood.mapper

import pt.socialfood.data.api.PlacesApi.Companion.buildImageUrl
import pt.socialfood.data.network.model.author.AuthorResponse
import pt.socialfood.data.network.model.guide.GuideResponse
import pt.socialfood.data.network.model.restaurant.RestaurantResponse
import pt.socialfood.data.network.model.search.SearchResponse
import pt.socialfood.domain.model.Search
import pt.socialfood.domain.model.SearchResultType

fun SearchResponse.toSearchResults(): List<Search> = authors.items.map { it.toSearchResult() } +
    guides.items.map { it.toSearchResult() } +
    restaurants.items.map { it.toSearchResult() }

private fun AuthorResponse.toSearchResult(): Search = Search(
    id = id,
    name = name,
    description = "@$username",
    imageUrl = imageUrl,
    type = SearchResultType.AUTHOR,
)

private fun GuideResponse.toSearchResult(): Search = Search(
    id = id,
    name = name,
    description = description,
    imageUrl = imageUrl,
    type = SearchResultType.GUIDE,
)

private fun RestaurantResponse.toSearchResult(): Search = Search(
    id = id,
    name = name,
    description = description.orEmpty(),
    imageUrl = photoNames.firstOrNull()?.let { buildImageUrl(it) },
    type = SearchResultType.RESTAURANT,
)
