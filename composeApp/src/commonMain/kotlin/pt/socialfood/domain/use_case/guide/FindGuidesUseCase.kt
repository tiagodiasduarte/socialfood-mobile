package pt.socialfood.domain.use_case.guide

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedGuides

interface FindGuidesUseCase {
    suspend operator fun invoke(page: Int, limit: Int, query: String? = null): Result<PagedGuides>
}
