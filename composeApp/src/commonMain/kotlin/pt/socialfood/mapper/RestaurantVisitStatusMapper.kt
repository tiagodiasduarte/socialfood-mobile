package pt.socialfood.mapper

import pt.socialfood.data.local.entity.RestaurantVisitStatusEntity
import pt.socialfood.data.local.entity.SyncState
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.RestaurantVisitStatus
import pt.socialfood.domain.model.VisitStatus

fun RestaurantVisitStatusEntity.toRestaurant(): Restaurant = Restaurant(
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

fun RestaurantVisitStatusEntity.toRestaurantVisitStatus(): RestaurantVisitStatus = RestaurantVisitStatus(
    restaurant = this.toRestaurant(),
    status = VisitStatus.valueOf(this.status),
    recordedAt = this.recordedAt,
)

fun Restaurant.toRestaurantVisitStatusEntity(
    status: VisitStatus,
    recordedAt: Long,
    syncState: SyncState,
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
    imageUrl = this.photoNames.firstOrNull(),
    status = status.name,
    recordedAt = recordedAt,
    syncState = syncState.name,
)
