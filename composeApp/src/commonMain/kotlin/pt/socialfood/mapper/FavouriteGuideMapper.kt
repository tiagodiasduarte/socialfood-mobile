package pt.socialfood.mapper

import pt.socialfood.data.local.entity.FavouriteGuideEntity
import pt.socialfood.data.local.entity.FavouriteSyncState
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.FavouriteGuide
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility

fun FavouriteGuideEntity.toGuide(): Guide = Guide(
    id = this.guideId,
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

fun FavouriteGuideEntity.toFavouriteGuide(): FavouriteGuide = FavouriteGuide(
    guide = this.toGuide(),
    favouritedAt = this.favouritedAt,
)

fun Guide.toFavouriteGuideEntity(
    favouritedAt: Long,
    syncState: FavouriteSyncState,
): FavouriteGuideEntity = FavouriteGuideEntity(
    guideId = this.id,
    name = this.name,
    description = this.description,
    visibility = this.visibility.name,
    authorId = this.author.id,
    authorName = this.author.name,
    authorUsername = this.author.username,
    authorImageUrl = this.author.imageUrl,
    numberOfRestaurant = this.numberOfRestaurant,
    imageUrl = this.imageUrl,
    favouritedAt = favouritedAt,
    syncState = syncState.name,
)
