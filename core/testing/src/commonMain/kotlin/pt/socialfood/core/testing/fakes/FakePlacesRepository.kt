package pt.socialfood.core.testing.fakes

import pt.socialfood.core.common.Result
import pt.socialfood.core.domain.repository.PlacesRepository
import pt.socialfood.core.model.Place

class FakePlacesRepository(private val result: Result<List<Place>> = Result.Success(emptyList())) :
    PlacesRepository {
    var searchInvokeCount: Int = 0
        private set
    var lastSearchQuery: String? = null
        private set

    override suspend fun search(query: String): Result<List<Place>> {
        searchInvokeCount++
        lastSearchQuery = query
        return result
    }
}
