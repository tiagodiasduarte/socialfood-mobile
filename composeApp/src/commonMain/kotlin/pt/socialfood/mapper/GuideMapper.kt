package pt.socialfood.mapper

import pt.socialfood.data.local.entity.GuideEntity
import pt.socialfood.data.network.model.author.AuthorDetailResponse
import pt.socialfood.data.network.model.guide.GuideDetailResponse
import pt.socialfood.data.network.model.guide.GuideResponse
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.AuthorDetail
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility

fun GuideResponse.toGuide() = Guide(
    id = this.id,
    name = this.name,
    description = this.description,
    visibility = this.visibility,
    author = this.author.toAuthor(),
    numberOfRestaurant = this.numberOfRestaurants,
    imageUrl = this.imageUrl,
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

fun Guide.toGuideEntity(scope: String, position: Int) = GuideEntity(
    id = this.id,
    scope = scope,
    name = this.name,
    description = this.description,
    visibility = this.visibility.name,
    authorId = this.author.id,
    authorName = this.author.name,
    authorUsername = this.author.username,
    authorImageUrl = this.author.imageUrl,
    numberOfRestaurant = this.numberOfRestaurant,
    imageUrl = this.imageUrl,
    position = position,
)

fun GuideEntity.toGuide() = Guide(
    id = this.id,
    name = this.name,
    description = this.description,
    visibility = GuideVisibility.valueOf(this.visibility),
    author = Author(
        id = this.authorId,
        name = this.authorName,
        username = this.authorUsername,
        imageUrl = this.authorImageUrl,
    ),
    numberOfRestaurant = this.numberOfRestaurant,
    imageUrl = this.imageUrl,
)
