package pt.socialfood.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

const val FAVOURITE_GUIDES_TABLE = "favourite_guides"

@Entity(tableName = FAVOURITE_GUIDES_TABLE)
data class FavouriteGuideEntity(
    @PrimaryKey val guideId: String,
    val name: String,
    val description: String,
    val visibility: String,
    val authorId: String,
    val authorName: String,
    val authorImageUrl: String?,
    val numberOfRestaurant: Int,
    val imageUrl: String?,
    val favouritedAt: Long,
    /** One of [FavouriteSyncState]'s `name`s. Stored as a raw String to avoid a Room TypeConverter. */
    val syncState: String,
)
