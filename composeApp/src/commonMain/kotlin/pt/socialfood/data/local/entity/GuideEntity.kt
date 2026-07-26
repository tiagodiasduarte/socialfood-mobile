package pt.socialfood.data.local.entity

import androidx.room.Entity

@Entity(tableName = "guides", primaryKeys = ["id", "scope"])
data class GuideEntity(
    val id: String,
    val scope: String,
    val name: String,
    val description: String,
    val visibility: String,
    val authorId: String,
    val authorName: String,
    val authorImageUrl: String?,
    val numberOfRestaurant: Int,
    val imageUrl: String?,
    val position: Int,
)
