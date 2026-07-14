package pt.socialfood.mapper

import pt.socialfood.data.network.model.author.AuthorDetailResponse
import pt.socialfood.data.network.model.guide.GuideDetailResponse
import pt.socialfood.data.network.model.guide.GuideResponse
import pt.socialfood.domain.model.AuthorDetail
import pt.socialfood.domain.model.Guide


fun GuideResponse.toGuide() = Guide(
    id = this.id,
    name = this.name,
    description = this.description,
    visibility = this.visibility,
    author = this.author.toAuthor(),
    numberOfRestaurant = this.numberOfRestaurants,
    imageUrl = this.imageUrl
)

fun AuthorDetailResponse.GuideResponse.toAuthorDetailGuide() = AuthorDetail.Guide(
    id = this.id,
    name = this.name,
    description = this.description,
    numberOfRestaurant = this.numberOfRestaurants,
    imageUrl = this.imageUrl,
)

fun GuideDetailResponse.toGuide() = Guide(
    id = this.id,
    name = this.name,
    description = this.description,
    visibility = this.visibility,
    author = this.author.toAuthor(),
    numberOfRestaurant = this.restaurants.size,
    restaurants = this.restaurants.map { it.toRestaurant() },
    imageUrl = this.imageUrl,
)