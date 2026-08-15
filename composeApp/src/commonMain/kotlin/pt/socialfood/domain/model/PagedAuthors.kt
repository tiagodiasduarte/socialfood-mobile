package pt.socialfood.domain.model

data class PagedAuthors(val authors: List<Author>, val page: Int, val hasMore: Boolean)
