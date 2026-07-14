package pt.socialfood.domain.use_case.home

import pt.socialfood.core.Result
import pt.socialfood.domain.model.HomeSection
import pt.socialfood.domain.model.HomeSectionType

interface CreateHomeSectionUseCase {
    suspend operator fun invoke(title: String, type: HomeSectionType, position: Int): Result<HomeSection>
}
