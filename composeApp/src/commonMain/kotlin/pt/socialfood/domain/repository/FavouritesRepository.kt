package pt.socialfood.domain.repository

import kotlinx.coroutines.flow.Flow
import pt.socialfood.core.Result
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.PagedFavouriteGuides

interface FavouritesRepository {
    suspend fun markFavourite(guide: Guide): Result<Unit>

    suspend fun unmarkFavourite(guideId: String): Result<Unit>

    suspend fun getFavouritesPaged(page: Int, limit: Int): Result<PagedFavouriteGuides>

    suspend fun isFavourite(guideId: String): Result<Boolean>

    // Local-cache-only reactive stream, same intentional exception to the Result<T> convention
    // as GuidesRepository.getGuidesPagingFlow — there's no network call or error surface to wrap.
    fun observeFavouriteGuideIds(): Flow<Set<String>>

    suspend fun syncFavourites(): Result<Unit>
}
