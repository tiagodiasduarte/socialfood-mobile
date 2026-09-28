package pt.socialfood.data.repository

import pt.socialfood.core.Result
import pt.socialfood.data.api.PlacesApi
import pt.socialfood.domain.error.safeApiCall
import pt.socialfood.domain.model.Place
import pt.socialfood.domain.repository.PlacesRepository
import pt.socialfood.mapper.toPlaces

class PlacesRepositoryImpl(private val placesApi: PlacesApi) : PlacesRepository {

    override suspend fun search(query: String): Result<List<Place>> = safeApiCall { placesApi.search(query).toPlaces() }
}
