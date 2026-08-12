package pt.socialfood.domain.usecase.home

import pt.socialfood.core.Result
import pt.socialfood.domain.model.HomeSection
import pt.socialfood.domain.repository.HomeRepository

class GetHomeSectionByIdUseCaseImpl(
    private val repository: HomeRepository,
) : GetHomeSectionByIdUseCase {
    override suspend operator fun invoke(id: String): Result<HomeSection> = repository.findById(id)
}
