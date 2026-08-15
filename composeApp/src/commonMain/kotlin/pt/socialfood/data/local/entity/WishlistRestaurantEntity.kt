package pt.socialfood.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

const val WISHLIST_RESTAURANTS_TABLE = "wishlist_restaurants"

@Entity(tableName = WISHLIST_RESTAURANTS_TABLE)
data class WishlistRestaurantEntity(
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
    val wishlistedAt: Long,
    /** One of [WishlistSyncState]'s `name`s. Stored as a raw String to avoid a Room TypeConverter. */
    val syncState: String,
)
