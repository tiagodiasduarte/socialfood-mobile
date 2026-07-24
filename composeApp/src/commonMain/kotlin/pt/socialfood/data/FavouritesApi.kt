package pt.socialfood.data

import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.favourite.FavouriteSyncResponse
import pt.socialfood.data.network.model.guide.GuideResponse

interface FavouritesApi {
    suspend fun markFavourite(guideId: String)

    suspend fun unmarkFavourite(guideId: String)

    suspend fun findFavouriteGuides(page: Int, limit: Int): PagedResponse<GuideResponse>

    suspend fun syncFavouriteGuides(since: String?): FavouriteSyncResponse
}
