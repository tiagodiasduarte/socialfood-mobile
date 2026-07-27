package pt.socialfood.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "author_remote_key")
data class AuthorRemoteKeyEntity(
    @PrimaryKey val id: Int = 0,
    val nextPage: Int?,
    val endOfPaginationReached: Boolean,
)
