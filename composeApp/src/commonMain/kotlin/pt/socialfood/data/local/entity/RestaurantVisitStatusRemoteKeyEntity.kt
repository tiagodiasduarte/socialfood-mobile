package pt.socialfood.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

const val RESTAURANT_VISIT_STATUS_REMOTE_KEYS_TABLE = "restaurant_visit_status_remote_keys"

@Entity(tableName = RESTAURANT_VISIT_STATUS_REMOTE_KEYS_TABLE)
data class RestaurantVisitStatusRemoteKeyEntity(
    @PrimaryKey val scope: String,
    val nextPage: Int?,
    val endOfPaginationReached: Boolean,
)
