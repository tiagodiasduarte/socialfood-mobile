package pt.socialfood.mapper

import pt.socialfood.data.network.model.restaurant.RestaurantResponse
import pt.socialfood.domain.model.Location
import pt.socialfood.domain.model.Restaurant

fun RestaurantResponse.toRestaurant(): Restaurant = Restaurant(
    id = this.id,
    name = this.name,
    description = this.description,
    city = this.city,
    country = this.country,
    countryCode = this.countryCode,
    postalCode = this.postalCode,
    imagesUrl = this.imagesUrl,
    address = this.address,
    rating = this.rating,
    userRatingCount = this.userRatingCount,
    websiteUrl = this.websiteUrl,
    phoneNumber = this.phoneNumber,
    regularOpeningHours = this.regularOpeningHours,
    location = Location(latitude = this.location.latitude, longitude = this.location.longitude),
)
