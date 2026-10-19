package pt.socialfood.mapper

import pt.socialfood.core.model.Location
import pt.socialfood.core.model.Restaurant
import pt.socialfood.core.model.RestaurantVisitStatus
import pt.socialfood.core.model.VisitStatus
import pt.socialfood.data.local.entity.RestaurantVisitStatusEntity
import pt.socialfood.data.local.entity.SyncState

fun RestaurantVisitStatusEntity.toRestaurant(): Restaurant = Restaurant(
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

fun RestaurantVisitStatusEntity.toRestaurantVisitStatus(): RestaurantVisitStatus = RestaurantVisitStatus(
    restaurant = this.toRestaurant(),
    status = VisitStatus.valueOf(this.status),
    recordedAt = this.recordedAt,
)

fun Restaurant.toRestaurantVisitStatusEntity(
    status: VisitStatus,
    recordedAt: Long,
    syncState: SyncState,
    position: Int,
): RestaurantVisitStatusEntity = RestaurantVisitStatusEntity(
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
    status = status.name,
    recordedAt = recordedAt,
    syncState = syncState.name,
    position = position,
)
