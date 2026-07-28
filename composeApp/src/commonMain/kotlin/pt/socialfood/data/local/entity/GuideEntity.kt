package pt.socialfood.data.local.entity

import androidx.room.Entity

const val GUIDES_TABLE = "guides"
const val GUIDES_PRIMARY_KEY_ID = "id"
const val GUIDES_PRIMARY_KEY_SCOPE = "scope"

@Entity(tableName = GUIDES_TABLE, primaryKeys = [GUIDES_PRIMARY_KEY_ID, GUIDES_PRIMARY_KEY_SCOPE])
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
