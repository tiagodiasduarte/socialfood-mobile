package pt.socialfood.data.repository

import io.ktor.client.plugins.ResponseException
import kotlinx.io.IOException
import pt.socialfood.core.Result
import pt.socialfood.data.api.PlacesApi
import pt.socialfood.data.network.extensions.toErrorEntity
import pt.socialfood.domain.model.Place
import pt.socialfood.domain.repository.PlacesRepository
import pt.socialfood.mapper.toPlaces

class PlacesRepositoryImpl(
    private val placesApi: PlacesApi,
) : PlacesRepository {

    override suspend fun search(query: String): Result<List<Place>> {
        return try {
            val places = placesApi.search(query).toPlaces()
            Result.Success(places)
        } catch (e: IOException) {
            Result.Error(e.toErrorEntity())
        } catch (e: ResponseException) {
            Result.Error(e.toErrorEntity())
        }
    }
}
