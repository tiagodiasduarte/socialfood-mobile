package pt.socialfood.data.api

import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.favourite.FavouriteSyncResponse
import pt.socialfood.data.network.model.guide.GuideResponse

interface FavouritesGuidesApi {
    suspend fun mark(guideId: String)

    suspend fun unmark(guideId: String)

    suspend fun find(page: Int, limit: Int): PagedResponse<GuideResponse>

    suspend fun sync(since: String?): FavouriteSyncResponse
}
