package pt.socialfood.domain.usecase.home

import pt.socialfood.core.Result
import pt.socialfood.domain.model.HomeSection

interface GetHomeSectionByIdUseCase {
    suspend operator fun invoke(id: String): Result<HomeSection>
}
