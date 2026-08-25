package pt.socialfood.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

const val FAVOURITE_RESTAURANTS_TABLE = "favourite_restaurants"

@Entity(tableName = FAVOURITE_RESTAURANTS_TABLE)
data class FavouriteRestaurantEntity(
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
    val latitude: Double,
    val longitude: Double,
    val favouritedAt: Long,
    /** One of [FavouriteSyncState]'s `name`s. Stored as a raw String to avoid a Room TypeConverter. */
    val syncState: String,
)
