package pt.socialfood.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

const val FAVOURITE_RESTAURANTS_REMOTE_KEYS_TABLE = "favourite_restaurants_remote_keys"

@Entity(tableName = FAVOURITE_RESTAURANTS_REMOTE_KEYS_TABLE)
data class FavouriteRestaurantRemoteKeyEntity(
    @PrimaryKey val scope: String,
    val nextPage: Int?,
    val endOfPaginationReached: Boolean,
)
