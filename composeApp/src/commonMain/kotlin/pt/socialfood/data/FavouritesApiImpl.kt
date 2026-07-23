package pt.socialfood.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.favourite.FavouriteGuideSyncResponse
import pt.socialfood.data.network.model.guide.GuideResponse

class FavouritesApiImpl(
    private val client: HttpClient
) : FavouritesApi {

    override suspend fun markFavourite(guideId: String) {
        client.post("guides/$guideId/favourite")
    }

    override suspend fun unmarkFavourite(guideId: String) {
        client.delete("guides/$guideId/favourite")
    }

    override suspend fun findFavouriteGuides(page: Int, limit: Int): PagedResponse<GuideResponse> =
        client.get("me/favourites/guides") {
            parameter("page", page)
            parameter("limit", limit)
        }.body()

    override suspend fun syncFavouriteGuides(since: String?): FavouriteGuideSyncResponse =
        client.get("me/favourites/guides/sync") {
            if (!since.isNullOrBlank()) parameter("since", since)
        }.body()
}
