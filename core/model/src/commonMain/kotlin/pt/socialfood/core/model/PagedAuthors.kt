package pt.socialfood.core.model

data class PagedAuthors(val authors: List<Author>, val page: Int, val hasMore: Boolean)
