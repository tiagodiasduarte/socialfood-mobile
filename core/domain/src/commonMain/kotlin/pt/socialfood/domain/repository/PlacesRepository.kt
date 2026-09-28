package pt.socialfood.domain.repository

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Place

interface PlacesRepository {
    suspend fun search(query: String): Result<List<Place>>
}
