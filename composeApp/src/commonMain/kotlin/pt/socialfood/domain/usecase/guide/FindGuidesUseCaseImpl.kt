package pt.socialfood.domain.usecase.guide

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedGuides
import pt.socialfood.domain.repository.GuidesRepository

class FindGuidesUseCaseImpl(
    private val guidesRepository: GuidesRepository,
) : FindGuidesUseCase {
    override suspend fun invoke(page: Int, limit: Int, query: String?, userId: String?): Result<PagedGuides> =
        guidesRepository.findGuidesPaged(page = page, limit = limit, query = query, userId = userId)
}
