package pt.socialfood.domain.model

data class RestaurantVisitStatus(val restaurant: Restaurant, val status: VisitStatus, val recordedAt: Long)
