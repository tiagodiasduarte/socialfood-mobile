package pt.socialfood.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "authors")
data class AuthorEntity(
    @PrimaryKey val id: String,
    val name: String,
    val imageUrl: String?,
    val position: Int,
)