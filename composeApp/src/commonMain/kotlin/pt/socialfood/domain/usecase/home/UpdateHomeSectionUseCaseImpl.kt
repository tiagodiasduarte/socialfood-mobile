package pt.socialfood.domain.usecase.home

import pt.socialfood.core.Result
import pt.socialfood.domain.model.HomeSection
import pt.socialfood.domain.repository.HomeRepository

class UpdateHomeSectionUseCaseImpl(
    private val repository: HomeRepository,
) : UpdateHomeSectionUseCase {
    override suspend operator fun invoke(
        id: String,
        title: String,
        position: Int,
        isActive: Boolean,
        restaurantIds: List<String>,
        guideIds: List<String>,
    ): Result<HomeSection> = repository.update(id, title, position, isActive, restaurantIds, guideIds)
}
