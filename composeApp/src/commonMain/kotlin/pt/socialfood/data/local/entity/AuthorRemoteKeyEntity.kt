package pt.socialfood.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

const val AUTHOR_REMOTE_KEY_TABLE = "author_remote_key"

@Entity(tableName = AUTHOR_REMOTE_KEY_TABLE)
data class AuthorRemoteKeyEntity(@PrimaryKey val id: Int = 0, val nextPage: Int?, val endOfPaginationReached: Boolean)
