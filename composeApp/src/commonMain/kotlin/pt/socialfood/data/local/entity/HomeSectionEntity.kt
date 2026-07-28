package pt.socialfood.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

const val HOME_SECTIONS_TABLE = "home_sections"

@Entity(tableName = HOME_SECTIONS_TABLE)
data class HomeSectionEntity(
    @PrimaryKey val id: String,
    val title: String,
    val type: String,
    val position: Int,
    val isActive: Boolean,
    val itemsJson: String,
)
