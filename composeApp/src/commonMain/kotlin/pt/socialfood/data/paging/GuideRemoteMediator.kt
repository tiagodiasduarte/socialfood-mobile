package pt.socialfood.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.immediateTransaction
import androidx.room.useWriterConnection
import pt.socialfood.data.GuidesApi
import pt.socialfood.data.local.AppDatabase
import pt.socialfood.data.local.dao.GuideDao
import pt.socialfood.data.local.dao.GuideRemoteKeyDao
import pt.socialfood.data.local.entity.GuideEntity
import pt.socialfood.data.local.entity.GuideRemoteKeyEntity
import pt.socialfood.mapper.toGuide
import pt.socialfood.mapper.toGuideEntity

/**
 * Scope key used for the unscoped ("all authors") Guides feed. Mirrors the `userId ?: GUIDES_ALL_SCOPE`
 * convention applied in [pt.socialfood.data.repository.GuidesRepositoryImpl.getGuidesPagingFlow].
 */
const val GUIDES_ALL_SCOPE = "ALL"

/**
 * Runs [block] as a single atomic write against the Guides cache.
 *
 * This is an injectable seam rather than a raw `AppDatabase` dependency for two reasons, both
 * specific to Room 2.8's Kotlin Multiplatform `room-runtime`:
 *  1. `RoomDatabase.withTransaction { }` (the classic Room-KTX helper) is only published for the
 *     Android target — it does not exist in `commonMain`, so [GuideRemoteMediator] (which must
 *     compile for iOS too) cannot call it directly. The KMP-portable equivalent is
 *     `RoomDatabase.useWriterConnection { }` + `Transactor.immediateTransaction { }`, used in
 *     [asGuideCacheTransactionRunner] below.
 *  2. `AppDatabase` cannot be constructed or faked from `commonTest` (it's Room-codegen backed
 *     per platform), so depending on it directly here would make [GuideRemoteMediator] untestable
 *     with the plain fakes this ticket calls for. Abstracting the transaction behind this
 *     functional interface keeps production wiring on the real `AppDatabase` (via
 *     [asGuideCacheTransactionRunner], invoked from Koin) while tests can supply a trivial
 *     pass-through implementation.
 */
fun interface GuideCacheTransactionRunner {
    suspend fun run(block: suspend () -> Unit)
}

/** Production [GuideCacheTransactionRunner] backed by a real [AppDatabase] transaction. */
fun AppDatabase.asGuideCacheTransactionRunner(): GuideCacheTransactionRunner =
    GuideCacheTransactionRunner { block ->
        useWriterConnection { transactor -> transactor.immediateTransaction { block() } }
    }

/**
 * Refreshes and pages the Guides cache from the network. Registered on a [androidx.paging.Pager]
 * alongside [GuideDao.pagingSource] so Room stays the single source of truth: [load] fetches from
 * [guidesApi] and writes into [guideDao]/[guideRemoteKeyDao], and those writes invalidate the
 * `PagingSource`, which is what actually drives the UI.
 */
@OptIn(ExperimentalPagingApi::class)
class GuideRemoteMediator(
    private val scope: String,
    private val guidesApi: GuidesApi,
    private val guideDao: GuideDao,
    private val guideRemoteKeyDao: GuideRemoteKeyDao,
    private val transactionRunner: GuideCacheTransactionRunner,
) : RemoteMediator<Int, GuideEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, GuideEntity>,
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKey = guideRemoteKeyDao.getByScope(scope)
                    remoteKey?.nextPage
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            val limit = state.config.pageSize
            val response = guidesApi.findGuides(
                page = page,
                limit = limit,
                userId = scope.takeIf { it != GUIDES_ALL_SCOPE },
            )
            val endOfPaginationReached = page * response.limit >= response.total
            val nextPage = if (endOfPaginationReached) null else page + 1

            transactionRunner.run {
                if (loadType == LoadType.REFRESH) {
                    guideDao.deleteByScope(scope)
                    guideRemoteKeyDao.deleteByScope(scope)
                }
                val entities = response.items.mapIndexed { index, guideResponse ->
                    guideResponse.toGuide().toGuideEntity(
                        scope = scope,
                        position = (page - 1) * limit + index,
                    )
                }
                guideDao.upsertAll(entities)
                guideRemoteKeyDao.upsert(
                    GuideRemoteKeyEntity(
                        scope = scope,
                        nextPage = nextPage,
                        endOfPaginationReached = endOfPaginationReached,
                    )
                )
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            MediatorResult.Error(exception)
        }
    }
}
