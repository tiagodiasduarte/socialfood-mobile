package pt.socialfood.mapper

import pt.socialfood.data.local.entity.WishlistRestaurantEntity
import pt.socialfood.data.local.entity.WishlistSyncState
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.WishlistRestaurant

fun WishlistRestaurantEntity.toRestaurant(): Restaurant = Restaurant(
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

fun WishlistRestaurantEntity.toWishlistRestaurant(): WishlistRestaurant = WishlistRestaurant(
    restaurant = this.toRestaurant(),
    wishlistedAt = this.wishlistedAt,
)

fun Restaurant.toWishlistRestaurantEntity(wishlistedAt: Long, syncState: WishlistSyncState): WishlistRestaurantEntity =
    WishlistRestaurantEntity(
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
        wishlistedAt = wishlistedAt,
        syncState = syncState.name,
    )
