package pt.socialfood.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.favourite.FavouriteSyncResponse
import pt.socialfood.data.network.model.guide.GuideResponse

class FavouritesGuidesApiImpl(private val client: HttpClient) : FavouritesGuidesApi {

    override suspend fun mark(guideId: String) {
        client.post("guides/$guideId/favourite")
    }

    override suspend fun unmark(guideId: String) {
        client.delete("guides/$guideId/favourite")
    }

    override suspend fun find(page: Int, limit: Int): PagedResponse<GuideResponse> =
        client.get("me/favourites/guides") {
            parameter("page", page)
            parameter("limit", limit)
        }.body()

    override suspend fun sync(since: String?): FavouriteSyncResponse = client.get("me/favourites/guides/sync") {
        if (!since.isNullOrBlank()) parameter("since", since)
    }.body()
}
