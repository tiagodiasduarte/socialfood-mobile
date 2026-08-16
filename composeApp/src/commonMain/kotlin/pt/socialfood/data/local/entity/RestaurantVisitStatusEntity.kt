package pt.socialfood.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

const val RESTAURANT_VISIT_STATUS_TABLE = "restaurant_visit_status"

/**
 * A restaurant's relationship to the current user: either on their wishlist or already
 * visited. A restaurant has at most one row here at a time — [status] tracks which.
 */
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
    /** One of [pt.socialfood.domain.model.VisitStatus]'s `name`s. */
    val status: String,
    val recordedAt: Long,
    /** One of [RestaurantVisitStatusSyncState]'s `name`s. Stored as a raw String to avoid a Room TypeConverter. */
    val syncState: String,
)
