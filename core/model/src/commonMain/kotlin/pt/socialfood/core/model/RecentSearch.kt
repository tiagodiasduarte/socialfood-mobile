package pt.socialfood.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class RecentSearchType { RESTAURANT, GUIDE, AUTHOR }

@Serializable
data class RecentSearch(val id: String, val type: RecentSearchType, val title: String)
