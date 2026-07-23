package pt.socialfood.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class FavouriteSyncState { SYNCED, PENDING_ADD, PENDING_REMOVE }

@Entity(tableName = "favourite_guides")
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
