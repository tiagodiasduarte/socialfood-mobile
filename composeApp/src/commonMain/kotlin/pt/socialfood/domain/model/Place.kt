package pt.socialfood.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Place(
    val id: String,
    val name: String,
    val address: String,
    val imageUrl: String? = null,
)