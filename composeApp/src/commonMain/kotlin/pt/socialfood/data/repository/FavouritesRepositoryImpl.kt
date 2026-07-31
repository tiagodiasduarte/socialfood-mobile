package pt.socialfood.data.repository

import androidx.sqlite.SQLiteException
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.io.IOException
import pt.socialfood.core.Result
import pt.socialfood.data.api.FavouritesApi
import pt.socialfood.data.local.dao.FavouriteDao
import pt.socialfood.data.local.entity.FavouriteSyncState
import pt.socialfood.data.network.extensions.toErrorEntity
import pt.socialfood.data.network.model.favourite.FavouriteSyncResponse
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.PagedFavouriteGuides
import pt.socialfood.domain.repository.FavouritesRepository
import pt.socialfood.domain.repository.SettingsRepository
import pt.socialfood.mapper.toFavouriteGuide
import pt.socialfood.mapper.toFavouriteGuideEntity
import pt.socialfood.mapper.toGuide
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private const val MIN_SYNC_INTERVAL_MS = 5 * 60 * 1000L

// The number of favourites a user can have is bounded, so one page covers the whole set —
// no need for true incremental pagination when hydrating newly-added favourites.
private const val MAX_FAVOURITES_FETCH = 500

class FavouritesRepositoryImpl(
    private val favouritesApi: FavouritesApi,
    private val favouriteDao: FavouriteDao,
    private val settingsRepository: SettingsRepository,
) : FavouritesRepository {

    override suspend fun markFavourite(guide: Guide): Result<Unit> {
        return try {
            val entity = guide.toFavouriteGuideEntity(
                favouritedAt = currentTimeMillis(),
                syncState = FavouriteSyncState.PENDING_ADD,
            )
            favouriteDao.upsert(entity)

            try {
                favouritesApi.markFavourite(guide.id)
                favouriteDao.updateSyncState(guide.id, FavouriteSyncState.SYNCED.name)
            } catch (_: Exception) {
                // Network failed — row stays PENDING_ADD, retried by the next syncFavourites().
            }

            Result.Success(Unit)
        } catch (e: SQLiteException) {
            Result.Error(e.toErrorEntity())
        }
    }

    override suspend fun unmarkFavourite(guideId: String): Result<Unit> {
        return try {
            favouriteDao.updateSyncState(guideId, FavouriteSyncState.PENDING_REMOVE.name)

            try {
                favouritesApi.unmarkFavourite(guideId)
                favouriteDao.deleteByGuideId(guideId)
            } catch (_: Exception) {
                // Network failed — row stays PENDING_REMOVE, retried by the next syncFavourites().
            }

            Result.Success(Unit)
        } catch (e: SQLiteException) {
            Result.Error(e.toErrorEntity())
        }
    }

    override suspend fun getFavouritesPaged(page: Int, limit: Int): Result<PagedFavouriteGuides> {
        return try {
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
            Result.Error(e.toErrorEntity())
        }
    }

    override suspend fun isFavourite(guideId: String): Result<Boolean> {
        return try {
            Result.Success(favouriteDao.getByGuideId(guideId) != null)
        } catch (e: SQLiteException) {
            Result.Error(e.toErrorEntity())
        }
    }

    override fun observeFavouriteGuideIds(): Flow<Set<String>> =
        favouriteDao.observeAllIds().map { it.toSet() }

    override suspend fun syncFavourites(): Result<Unit> {
        return try {
            val now = currentTimeMillis()
            val lastAttempt = settingsRepository.getLastFavouritesSyncAttemptAt()
            if (lastAttempt != null && now - lastAttempt < MIN_SYNC_INTERVAL_MS) {
                return Result.Success(Unit)
            }
            settingsRepository.saveLastFavouritesSyncAttemptAt(now)

            pushPendingMutations()

            val checkpoint = settingsRepository.getFavouritesSyncCheckpoint()
            val changes = favouritesApi.syncFavouriteGuides(since = checkpoint)

            applyChanges(changes)

            settingsRepository.saveFavouritesSyncCheckpoint(changes.nextCheckpoint)
            Result.Success(Unit)
        } catch (e: IOException) {
            Result.Error(e.toErrorEntity())
        } catch (e: ResponseException) {
            Result.Error(e.toErrorEntity())
        } catch (e: SQLiteException) {
            Result.Error(e.toErrorEntity())
        }
    }

    private suspend fun pushPendingMutations() {
        favouriteDao.getPending().forEach { entity ->
            when (FavouriteSyncState.valueOf(entity.syncState)) {
                FavouriteSyncState.PENDING_ADD -> try {
                    favouritesApi.markFavourite(entity.guideId)
                    favouriteDao.updateSyncState(entity.guideId, FavouriteSyncState.SYNCED.name)
                } catch (_: Exception) {
                    // Still offline/failing — retried next sync.
                }

                FavouriteSyncState.PENDING_REMOVE -> try {
                    favouritesApi.unmarkFavourite(entity.guideId)
                    favouriteDao.deleteByGuideId(entity.guideId)
                } catch (_: Exception) {
                    // Still offline/failing — retried next sync.
                }

                FavouriteSyncState.SYNCED -> Unit
            }
        }
    }

    private suspend fun applyChanges(changes: FavouriteSyncResponse) {
        if (changes.removedIds.isNotEmpty()) {
            favouriteDao.deleteByGuideIds(changes.removedIds)
        }

        if (changes.addedIds.isNotEmpty()) {
            val addedIds = changes.addedIds.toSet()
            val now = currentTimeMillis()
            val allFavourites = favouritesApi.findFavouriteGuides(page = 1, limit = MAX_FAVOURITES_FETCH)
            val toUpsert = allFavourites.items
                .filter { it.id in addedIds }
                .map { it.toGuide().toFavouriteGuideEntity(favouritedAt = now, syncState = FavouriteSyncState.SYNCED) }
            favouriteDao.upsertAll(toUpsert)
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun currentTimeMillis(): Long = Clock.System.now().toEpochMilliseconds()
}
