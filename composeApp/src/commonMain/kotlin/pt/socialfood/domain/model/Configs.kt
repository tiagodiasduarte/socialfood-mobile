package pt.socialfood.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Configs(
    val version: String,
)
