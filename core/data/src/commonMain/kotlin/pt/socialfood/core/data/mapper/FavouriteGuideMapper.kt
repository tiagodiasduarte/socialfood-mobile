package pt.socialfood.core.data.mapper

import pt.socialfood.core.database.entity.FavouriteGuideEntity
import pt.socialfood.core.database.entity.FavouriteSyncState
import pt.socialfood.core.model.Author
import pt.socialfood.core.model.Guide
import pt.socialfood.core.model.GuideVisibility

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
        isPublic = this.authorIsPublic,
    ),
    numberOfRestaurant = this.numberOfRestaurant,
    imageUrl = this.imageUrl,
    shareCode = this.shareCode,
)

fun Guide.toFavouriteGuideEntity(
    favouritedAt: Long,
    syncState: FavouriteSyncState,
    position: Int,
): FavouriteGuideEntity = FavouriteGuideEntity(
    guideId = this.id,
    name = this.name,
    description = this.description,
    visibility = this.visibility.name,
    authorId = this.author.id,
    authorName = this.author.name,
    authorUsername = this.author.username,
    authorImageUrl = this.author.imageUrl,
    authorIsPublic = this.author.isPublic,
    numberOfRestaurant = this.numberOfRestaurant,
    imageUrl = this.imageUrl,
    favouritedAt = favouritedAt,
    syncState = syncState.name,
    position = position,
    shareCode = this.shareCode,
)
