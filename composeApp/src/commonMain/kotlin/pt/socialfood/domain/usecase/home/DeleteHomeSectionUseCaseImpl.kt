package pt.socialfood.domain.usecase.home

import pt.socialfood.core.Result
import pt.socialfood.domain.repository.HomeRepository

class DeleteHomeSectionUseCaseImpl(
    private val repository: HomeRepository,
) : DeleteHomeSectionUseCase {
    override suspend operator fun invoke(id: String): Result<Boolean> = repository.delete(id)
}
