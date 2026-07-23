package pt.socialfood.domain.repository

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.PagedFavouriteGuides

interface FavouritesRepository {
    suspend fun markFavourite(guide: Guide): Result<Unit>

    suspend fun unmarkFavourite(guideId: String): Result<Unit>

    suspend fun getFavouritesPaged(page: Int, limit: Int): Result<PagedFavouriteGuides>

    suspend fun syncFavourites(): Result<Unit>
}
