package pt.socialfood.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

const val GUIDE_REMOTE_KEYS_TABLE = "guide_remote_keys"

@Entity(tableName = GUIDE_REMOTE_KEYS_TABLE)
data class GuideRemoteKeyEntity(@PrimaryKey val scope: String, val nextPage: Int?, val endOfPaginationReached: Boolean)
