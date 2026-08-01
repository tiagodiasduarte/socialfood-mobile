package pt.socialfood.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

const val AUTHORS_TABLE = "authors"

@Entity(tableName = AUTHORS_TABLE)
data class AuthorEntity(
    @PrimaryKey val id: String,
    val name: String,
    val username: String,
    val imageUrl: String?,
    val position: Int,
)
