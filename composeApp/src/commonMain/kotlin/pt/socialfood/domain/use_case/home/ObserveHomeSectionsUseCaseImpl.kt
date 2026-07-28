package pt.socialfood.domain.use_case.home

import kotlinx.coroutines.flow.Flow
import pt.socialfood.domain.model.HomeSection
import pt.socialfood.domain.repository.HomeRepository

class ObserveHomeSectionsUseCaseImpl(
    private val repository: HomeRepository,
) : ObserveHomeSectionsUseCase {
    override operator fun invoke(): Flow<List<HomeSection>> = repository.observeHomeSections()
}
