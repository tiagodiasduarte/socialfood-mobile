package pt.socialfood.domain.model

data class PagedGuides(val guides: List<Guide>, val page: Int, val total: Int, val hasMore: Boolean)
