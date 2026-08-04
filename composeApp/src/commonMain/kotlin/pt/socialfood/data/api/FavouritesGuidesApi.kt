package pt.socialfood.data.api

import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.favourite.FavouriteSyncResponse
import pt.socialfood.data.network.model.guide.GuideResponse

interface FavouritesGuidesApi {
    suspend fun markFavourite(guideId: String)

    suspend fun unmarkFavourite(guideId: String)

    suspend fun findFavouriteGuides(page: Int, limit: Int): PagedResponse<GuideResponse>

    suspend fun syncFavouriteGuides(since: String?): FavouriteSyncResponse
}
