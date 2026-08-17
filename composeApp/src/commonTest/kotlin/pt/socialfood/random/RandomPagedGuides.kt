package pt.socialfood.random

import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.PagedGuides
import kotlin.random.Random

fun Random.nextPagedGuides(
    guides: List<Guide> = nextList { nextGuide() },
    page: Int = nextInt(1, 10),
    total: Int = guides.size,
    hasMore: Boolean = nextBoolean(),
) = PagedGuides(guides = guides, page = page, total = total, hasMore = hasMore)
