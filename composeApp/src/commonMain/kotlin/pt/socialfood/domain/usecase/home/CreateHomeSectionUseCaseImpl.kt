package pt.socialfood.domain.usecase.home

import pt.socialfood.core.Result
import pt.socialfood.domain.model.HomeSection
import pt.socialfood.domain.model.HomeSectionType
import pt.socialfood.domain.repository.HomeRepository

class CreateHomeSectionUseCaseImpl(
    private val repository: HomeRepository,
) : CreateHomeSectionUseCase {
    override suspend operator fun invoke(title: String, type: HomeSectionType, position: Int): Result<HomeSection> =
        repository.create(title, type, position)
}
