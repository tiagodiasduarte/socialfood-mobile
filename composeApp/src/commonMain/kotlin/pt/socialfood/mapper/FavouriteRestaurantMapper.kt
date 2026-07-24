package pt.socialfood.mapper

import pt.socialfood.data.local.entity.FavouriteRestaurantEntity
import pt.socialfood.data.local.entity.FavouriteSyncState
import pt.socialfood.domain.model.FavouriteRestaurant
import pt.socialfood.domain.model.Restaurant

fun FavouriteRestaurantEntity.toRestaurant(): Restaurant = Restaurant(
    id = this.restaurantId,
    name = this.name,
    description = this.description,
    city = this.city,
    country = this.country,
    countryCode = this.countryCode,
    postalCode = this.postalCode,
    photoNames = listOfNotNull(this.imageUrl),
    address = this.address,
    rating = this.rating,
    userRatingCount = this.userRatingCount,
    websiteUrl = this.websiteUrl,
    phoneNumber = this.phoneNumber,
)

fun FavouriteRestaurantEntity.toFavouriteRestaurant(): FavouriteRestaurant = FavouriteRestaurant(
    restaurant = this.toRestaurant(),
    favouritedAt = this.favouritedAt,
)

fun Restaurant.toFavouriteRestaurantEntity(
    favouritedAt: Long,
    syncState: FavouriteSyncState,
): FavouriteRestaurantEntity = FavouriteRestaurantEntity(
    restaurantId = this.id,
    name = this.name,
    description = this.description,
    city = this.city,
    country = this.country,
    countryCode = this.countryCode,
    postalCode = this.postalCode,
    address = this.address,
    rating = this.rating,
    userRatingCount = this.userRatingCount,
    websiteUrl = this.websiteUrl,
    phoneNumber = this.phoneNumber,
    imageUrl = this.photoNames.firstOrNull(),
    favouritedAt = favouritedAt,
    syncState = syncState.name,
)
