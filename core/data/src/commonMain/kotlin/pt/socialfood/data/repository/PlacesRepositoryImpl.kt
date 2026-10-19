package pt.socialfood.data.repository

import pt.socialfood.core.common.Result
import pt.socialfood.core.domain.repository.PlacesRepository
import pt.socialfood.core.model.Place
import pt.socialfood.data.api.PlacesApi
import pt.socialfood.domain.error.safeApiCall
import pt.socialfood.mapper.toPlaces

internal class PlacesRepositoryImpl(private val placesApi: PlacesApi) : PlacesRepository {

    override suspend fun search(query: String): Result<List<Place>> = safeApiCall { placesApi.search(query).toPlaces() }
}
