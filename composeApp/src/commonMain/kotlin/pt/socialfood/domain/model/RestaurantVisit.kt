package pt.socialfood.domain.model

data class RestaurantVisit(val restaurant: Restaurant, val status: RestaurantVisitStatus, val recordedAt: Long)
