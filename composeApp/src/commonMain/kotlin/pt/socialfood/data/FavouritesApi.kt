package pt.socialfood.data

import pt.socialfood.data.network.model.favourite.FavouriteChangesResponse

interface FavouritesApi {
    suspend fun markFavourite(guideId: String)

    suspend fun unmarkFavourite(guideId: String)

    suspend fun findFavouriteChanges(since: String?, limit: Int): FavouriteChangesResponse
}
