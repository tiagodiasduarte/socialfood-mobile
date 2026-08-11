package pt.socialfood.domain.usecase.guide

import pt.socialfood.core.Result
import pt.socialfood.domain.repository.GuidesRepository

class DeleteGuideUseCaseImpl(
    private val repository: GuidesRepository,
) : DeleteGuideUseCase {
    override suspend operator fun invoke(id: String): Result<Boolean> = repository.delete(id)
}
