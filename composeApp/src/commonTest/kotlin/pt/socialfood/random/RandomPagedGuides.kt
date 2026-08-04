package pt.socialfood.random

import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.PagedGuides

fun randomPagedGuides(
    guides: List<Guide> = randomList { randomGuide() },
    page: Int = randomInt(1, 10),
    total: Int = guides.size,
    hasMore: Boolean = randomBoolean(),
) = PagedGuides(guides = guides, page = page, total = total, hasMore = hasMore)
