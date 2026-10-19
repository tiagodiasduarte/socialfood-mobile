package pt.socialfood.core.domain.repository

import pt.socialfood.core.common.Result
import pt.socialfood.core.model.Place

interface PlacesRepository {
    suspend fun search(query: String): Result<List<Place>>
}
