package pt.socialfood.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "guide_remote_keys")
data class GuideRemoteKeyEntity(
    @PrimaryKey val scope: String,
    val nextPage: Int?,
    val endOfPaginationReached: Boolean,
)
