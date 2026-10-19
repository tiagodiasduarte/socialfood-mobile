package pt.socialfood.core.data.repository

import pt.socialfood.core.common.Result
import pt.socialfood.core.data.mapper.toPlaces
import pt.socialfood.core.domain.repository.PlacesRepository
import pt.socialfood.core.model.Place
import pt.socialfood.core.network.api.PlacesApi
import pt.socialfood.core.network.safeApiCall

internal class PlacesRepositoryImpl(private val placesApi: PlacesApi) : PlacesRepository {

    override suspend fun search(query: String): Result<List<Place>> = safeApiCall { placesApi.search(query).toPlaces() }
}
