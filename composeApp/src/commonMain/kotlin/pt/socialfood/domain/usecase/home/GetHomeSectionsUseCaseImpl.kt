package pt.socialfood.domain.usecase.home

import pt.socialfood.core.Result
import pt.socialfood.domain.model.HomeSection
import pt.socialfood.domain.repository.HomeRepository

class GetHomeSectionsUseCaseImpl(private val repository: HomeRepository) : GetHomeSectionsUseCase {
    override suspend operator fun invoke(): Result<List<HomeSection>> = repository.findAll()
}
