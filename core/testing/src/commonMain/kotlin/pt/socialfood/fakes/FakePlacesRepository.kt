package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Place
import pt.socialfood.domain.repository.PlacesRepository

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
