package pt.socialfood.domain.use_case

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Place
import pt.socialfood.domain.repository.PlacesRepository

class SearchPlacesUseCaseImpl(
    private val repository: PlacesRepository,
) : SearchPlacesUseCase {
    override suspend operator fun invoke(query: String): Result<List<Place>> = repository.search(query)
}
