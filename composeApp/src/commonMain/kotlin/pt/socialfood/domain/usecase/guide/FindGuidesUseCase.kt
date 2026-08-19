package pt.socialfood.domain.usecase.guide

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedGuides

interface FindGuidesUseCase {
    suspend operator fun invoke(
        page: Int,
        limit: Int,
        query: String? = null,
        userId: String? = null,
    ): Result<PagedGuides>
}
