package pt.socialfood.mapper

import pt.socialfood.data.local.entity.RestaurantVisitEntity
import pt.socialfood.data.local.entity.RestaurantVisitSyncState
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.RestaurantVisitStatus
import pt.socialfood.domain.model.VisitStatus

fun RestaurantVisitEntity.toRestaurant(): Restaurant = Restaurant(
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

fun RestaurantVisitEntity.toRestaurantVisitStatus(): RestaurantVisitStatus = RestaurantVisitStatus(
    restaurant = this.toRestaurant(),
    status = VisitStatus.valueOf(this.status),
    recordedAt = this.recordedAt,
)

fun Restaurant.toRestaurantVisitEntity(
    status: VisitStatus,
    recordedAt: Long,
    syncState: RestaurantVisitSyncState,
): RestaurantVisitEntity = RestaurantVisitEntity(
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
