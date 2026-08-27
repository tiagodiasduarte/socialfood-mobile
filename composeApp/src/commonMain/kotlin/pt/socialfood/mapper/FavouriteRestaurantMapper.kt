package pt.socialfood.mapper

import pt.socialfood.data.local.entity.FavouriteRestaurantEntity
import pt.socialfood.data.local.entity.FavouriteSyncState
import pt.socialfood.domain.model.Location
import pt.socialfood.domain.model.Restaurant

fun FavouriteRestaurantEntity.toRestaurant(): Restaurant = Restaurant(
    id = this.restaurantId,
    name = this.name,
    description = this.description,
    city = this.city,
    country = this.country,
    countryCode = this.countryCode,
    postalCode = this.postalCode,
    imagesUrl = listOfNotNull(this.imageUrl),
    address = this.address,
    rating = this.rating,
    userRatingCount = this.userRatingCount,
    websiteUrl = this.websiteUrl,
    phoneNumber = this.phoneNumber,
    location = Location(latitude = this.latitude, longitude = this.longitude),
)

fun Restaurant.toFavouriteRestaurantEntity(
    favouritedAt: Long,
    syncState: FavouriteSyncState,
    position: Int,
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
    imageUrl = this.imagesUrl.firstOrNull(),
    latitude = this.location.latitude,
    longitude = this.location.longitude,
    favouritedAt = favouritedAt,
    syncState = syncState.name,
    position = position,
)
