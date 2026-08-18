package pt.socialfood.data.repository

import androidx.sqlite.SQLiteException
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pt.socialfood.core.Result
import pt.socialfood.data.api.FavouritesGuidesApi
import pt.socialfood.data.currentTimeMillis
import pt.socialfood.data.local.dao.FavouriteDao
import pt.socialfood.data.local.entity.FavouriteSyncState
import pt.socialfood.data.network.extensions.toDataError
import pt.socialfood.data.network.model.favourite.FavouriteSyncResponse
import pt.socialfood.domain.error.safeApiCall
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.PagedFavouriteGuides
import pt.socialfood.domain.repository.FavouritesGuidesRepository
import pt.socialfood.domain.repository.SettingsRepository
import pt.socialfood.mapper.toFavouriteGuide
import pt.socialfood.mapper.toFavouriteGuideEntity
import pt.socialfood.mapper.toGuide

private const val MIN_SYNC_INTERVAL_MS = 5 * 60 * 1000L

// The number of favourites a user can have is bounded, so one page covers the whole set —
// no need for true incremental pagination when hydrating newly-added favourites.
private const val MAX_FAVOURITES_FETCH = 500

@Suppress("TooManyFunctions")
class FavouritesGuidesRepositoryImpl(
    private val favouritesApi: FavouritesGuidesApi,
    private val favouriteDao: FavouriteDao,
    private val settingsRepository: SettingsRepository,
) : FavouritesGuidesRepository {

    private val logger = Logger.withTag("FavouritesGuidesRepository")

    override suspend fun markFavourite(guide: Guide): Result<Unit> = try {
        val entity = guide.toFavouriteGuideEntity(
            favouritedAt = currentTimeMillis(),
            syncState = FavouriteSyncState.PENDING_ADD,
        )
        favouriteDao.upsert(entity)

        when (val result = safeApiCall { favouritesApi.markFavourite(guide.id) }) {
            is Result.Failure ->
                logger.w {
                    "markFavourite(${guide.id}) failed (${result.error}); " +
                        "row stays PENDING_ADD, retried by the next syncFavourites()."
                }
            is Result.Success -> {
                favouriteDao.updateSyncState(guide.id, FavouriteSyncState.SYNCED.name)
            }
        }

        Result.Success(Unit)
    } catch (e: SQLiteException) {
        Result.Failure(e.toDataError())
    }

    override suspend fun unmarkFavourite(guideId: String): Result<Unit> = try {
        favouriteDao.updateSyncState(guideId, FavouriteSyncState.PENDING_REMOVE.name)

        when (val result = safeApiCall { favouritesApi.unmarkFavourite(guideId) }) {
            is Result.Failure ->
                logger.w {
                    "unmarkFavourite($guideId) failed (${result.error}); " +
                        "row stays PENDING_REMOVE, retried by the next syncFavourites()."
                }
            is Result.Success -> {
                favouriteDao.deleteByGuideId(guideId)
            }
        }

        Result.Success(Unit)
    } catch (e: SQLiteException) {
        Result.Failure(e.toDataError())
    }

    override suspend fun getFavouritesPaged(page: Int, limit: Int): Result<PagedFavouriteGuides> = try {
        val offset = (page - 1) * limit
        val entities = favouriteDao.getPaged(limit = limit, offset = offset)
        val total = favouriteDao.countAll()
        Result.Success(
            PagedFavouriteGuides(
                favourites = entities.map { it.toFavouriteGuide() },
                page = page,
                total = total,
                hasMore = page * limit < total,
            ),
        )
    } catch (e: SQLiteException) {
        Result.Failure(e.toDataError())
    }

    override suspend fun isFavourite(guideId: String): Result<Boolean> = try {
        Result.Success(favouriteDao.getByGuideId(guideId) != null)
    } catch (e: SQLiteException) {
        Result.Failure(e.toDataError())
    }

    override fun observeFavouriteGuideIds(): Flow<Set<String>> = favouriteDao.observeAllIds().map { it.toSet() }

    @Suppress("ReturnCount")
    override suspend fun syncFavourites(): Result<Unit> {
        val now = currentTimeMillis()
        val lastAttempt = settingsRepository.getLastFavouritesSyncAttemptAt()
        if (lastAttempt != null && now - lastAttempt < MIN_SYNC_INTERVAL_MS) {
            return Result.Success(Unit)
        }

        return try {
            settingsRepository.saveLastFavouritesSyncAttemptAt(now)

            pushPendingMutations()

            val syncedAt = settingsRepository.getLastFavouritesSyncedAt()
            val changes = when (val result = safeApiCall { favouritesApi.syncFavouriteGuides(since = syncedAt) }) {
                is Result.Failure -> return result
                is Result.Success -> result.data
            }

            val applyResult = applyChanges(changes)
            if (applyResult is Result.Failure) return applyResult

            settingsRepository.saveLastFavouritesSyncedAt(changes.syncedAt)
            Result.Success(Unit)
        } catch (e: SQLiteException) {
            Result.Failure(e.toDataError())
        }
    }

    private suspend fun pushPendingMutations() {
        favouriteDao.getPending().forEach { entity ->
            when (FavouriteSyncState.valueOf(entity.syncState)) {
                FavouriteSyncState.PENDING_ADD -> pushPendingAdd(entity.guideId)
                FavouriteSyncState.PENDING_REMOVE -> pushPendingRemove(entity.guideId)
                FavouriteSyncState.SYNCED -> Unit
            }
        }
    }

    private suspend fun pushPendingAdd(guideId: String) {
        try {
            when (val result = safeApiCall { favouritesApi.markFavourite(guideId) }) {
                is Result.Failure ->
                    logger.w { "markFavourite($guideId) still failing (${result.error}); retried next sync." }
                is Result.Success ->
                    favouriteDao.updateSyncState(guideId, FavouriteSyncState.SYNCED.name)
            }
        } catch (e: SQLiteException) {
            logger.w(e) { "markFavourite($guideId) local update failed; retried next sync." }
        }
    }

    private suspend fun pushPendingRemove(guideId: String) {
        try {
            when (val result = safeApiCall { favouritesApi.unmarkFavourite(guideId) }) {
                is Result.Failure ->
                    logger.w { "unmarkFavourite($guideId) still failing (${result.error}); retried next sync." }
                is Result.Success ->
                    favouriteDao.deleteByGuideId(guideId)
            }
        } catch (e: SQLiteException) {
            logger.w(e) { "unmarkFavourite($guideId) local update failed; retried next sync." }
        }
    }

    private suspend fun applyChanges(changes: FavouriteSyncResponse): Result<Unit> {
        if (changes.removedIds.isNotEmpty()) {
            favouriteDao.deleteByGuideIds(changes.removedIds)
        }

        if (changes.addedIds.isNotEmpty()) {
            val addedIds = changes.addedIds.toSet()
            val now = currentTimeMillis()
            val allFavourites = when (
                val result = safeApiCall { favouritesApi.findFavouriteGuides(page = 1, limit = MAX_FAVOURITES_FETCH) }
            ) {
                is Result.Failure -> return result
                is Result.Success -> result.data
            }
            val toUpsert = allFavourites.items
                .filter { it.id in addedIds }
                .map { it.toGuide().toFavouriteGuideEntity(favouritedAt = now, syncState = FavouriteSyncState.SYNCED) }
            favouriteDao.upsertAll(toUpsert)
        }

        return Result.Success(Unit)
    }
}
