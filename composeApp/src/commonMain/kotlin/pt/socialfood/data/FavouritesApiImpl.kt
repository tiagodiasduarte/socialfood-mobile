package pt.socialfood.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import pt.socialfood.data.network.model.favourite.FavouriteChangesResponse

class FavouritesApiImpl(
    private val client: HttpClient
) : FavouritesApi {

    override suspend fun markFavourite(guideId: String) {
        client.post("guides/$guideId/favourite")
    }

    override suspend fun unmarkFavourite(guideId: String) {
        client.delete("guides/$guideId/favourite")
    }

    override suspend fun findFavouriteChanges(since: String?, limit: Int): FavouriteChangesResponse =
        client.get("favourites/changes") {
            if (!since.isNullOrBlank()) parameter("since", since)
            parameter("limit", limit)
        }.body()
}
