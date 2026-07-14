package pt.socialfood.domain.use_case

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Place

interface SearchPlacesUseCase {
    suspend operator fun invoke(query: String): Result<List<Place>>
}
