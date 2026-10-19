package pt.socialfood.core.network.model.guide

import kotlinx.serialization.Serializable

@Serializable
data class JoinGuideRequest(val code: String)
