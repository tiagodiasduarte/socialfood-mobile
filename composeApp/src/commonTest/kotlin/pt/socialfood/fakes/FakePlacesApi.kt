package pt.socialfood.fakes

import pt.socialfood.data.api.PlacesApi
import pt.socialfood.data.network.model.place.PlaceResponse

class FakePlacesApi(private val shouldThrow: Boolean = false) : PlacesApi {

    override suspend fun search(query: String): PlaceResponse {
        if (shouldThrow) throw FakeException("test error")
        return PlaceResponse(
            results = listOf(
                PlaceResponse.Place(
                    id = "place-id",
                    name = "Place Name",
                    address = "Rua Example, 1",
                )
            )
        )
    }
}
