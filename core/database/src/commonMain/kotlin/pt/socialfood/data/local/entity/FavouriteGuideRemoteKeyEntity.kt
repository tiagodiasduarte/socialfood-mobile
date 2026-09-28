package pt.socialfood.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

const val FAVOURITE_GUIDES_REMOTE_KEYS_TABLE = "favourite_guides_remote_keys"

@Entity(tableName = FAVOURITE_GUIDES_REMOTE_KEYS_TABLE)
data class FavouriteGuideRemoteKeyEntity(
    @PrimaryKey val scope: String,
    val nextPage: Int?,
    val endOfPaginationReached: Boolean,
)
