package pt.socialfood.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class PagedResponse<T>(val items: List<T>, val page: Int, val limit: Int, val total: Int)
