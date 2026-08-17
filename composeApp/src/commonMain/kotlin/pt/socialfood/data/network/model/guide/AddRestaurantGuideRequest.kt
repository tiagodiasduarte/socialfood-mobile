package pt.socialfood.data.network.model.guide

import kotlinx.serialization.Serializable

@Serializable
data class AddRestaurantGuideRequest(val guideId: String, val placeId: String?)
