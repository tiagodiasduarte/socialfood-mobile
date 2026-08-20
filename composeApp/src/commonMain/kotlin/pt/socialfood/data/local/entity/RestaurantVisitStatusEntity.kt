package pt.socialfood.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

const val RESTAURANT_VISIT_STATUS_TABLE = "restaurant_visit_status"

@Entity(tableName = RESTAURANT_VISIT_STATUS_TABLE)
data class RestaurantVisitStatusEntity(
    @PrimaryKey val restaurantId: String,
    val name: String,
    val description: String?,
    val city: String,
    val country: String,
    val countryCode: String,
    val postalCode: String?,
    val address: String,
    val rating: Double,
    val userRatingCount: Int,
    val websiteUrl: String?,
    val phoneNumber: String,
    val imageUrl: String?,
    val status: String,
    val recordedAt: Long,
    val syncState: String,
    val position: Int,
)
