package pt.socialfood.core.network.api

import pt.socialfood.core.network.model.PagedResponse
import pt.socialfood.core.network.model.favourite.FavouriteSyncResponse
import pt.socialfood.core.network.model.guide.GuideResponse

interface FavouritesGuidesApi {
    suspend fun mark(guideId: String)

    suspend fun unmark(guideId: String)

    suspend fun find(page: Int, limit: Int): PagedResponse<GuideResponse>

    suspend fun sync(since: String?): FavouriteSyncResponse<GuideResponse>
}
