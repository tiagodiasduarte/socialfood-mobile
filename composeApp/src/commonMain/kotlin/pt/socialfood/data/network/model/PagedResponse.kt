package pt.socialfood.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class PagedResponse<T>(val items: List<T>, val page: Int, val limit: Int, val total: Int)
