package pt.socialfood.data.repository

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.data.local.entity.FavouriteSyncState
import pt.socialfood.data.paging.FavouriteGuideCacheTransactionRunner
import pt.socialfood.domain.model.Guide
import pt.socialfood.fakes.FakeFavouriteDao
import pt.socialfood.fakes.FakeFavouriteGuideRemoteKeyDao
import pt.socialfood.fakes.FakeFavouritesGuidesApi
import pt.socialfood.fakes.FakeSettingsRepository
import pt.socialfood.mapper.toFavouriteGuideEntity
import pt.socialfood.random.nextGuide
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class FavouritesGuidesRepositoryImplTest {
    private val fakeGuide = Random.nextGuide()

    @OptIn(ExperimentalTime::class)
    private fun now(): Long = Clock.System.now().toEpochMilliseconds()

    private fun createRepository(
        api: FakeFavouritesGuidesApi = FakeFavouritesGuidesApi(),
        dao: FakeFavouriteDao = FakeFavouriteDao(),
        settings: FakeSettingsRepository = FakeSettingsRepository(),
    ): Triple<FavouritesGuidesRepositoryImpl, FakeFavouriteDao, FakeSettingsRepository> = Triple(
        FavouritesGuidesRepositoryImpl(
            favouritesApi = api,
            favouriteDao = dao,
            favouriteGuideRemoteKeyDao = FakeFavouriteGuideRemoteKeyDao(),
            transactionRunner = FavouriteGuideCacheTransactionRunner { it() },
            settingsRepository = settings,
        ),
        dao,
        settings,
    )

    // markFavourite

    @Test
    fun `given api succeeds when markFavourite is called then persists SYNCED entity and returns Success`() = runTest {
        // Given
        val (repo, dao, _) = createRepository()

        // When
        val result = repo.mark(fakeGuide)

        // Then
        assertIs<Result.Success<Unit>>(result)
        val stored = dao.getByGuideId(fakeGuide.id)
        assertEquals(FavouriteSyncState.SYNCED.name, stored?.syncState)
    }

    @Test
    fun `given api throws when markFavourite is called then still returns Success with entity left PENDING_ADD`() =
        runTest {
            // Given
            val (repo, dao, _) = createRepository(api = FakeFavouritesGuidesApi(shouldThrow = true))

            // When
            val result = repo.mark(fakeGuide)

            // Then
            assertIs<Result.Success<Unit>>(result)
            val stored = dao.getByGuideId(fakeGuide.id)
            assertEquals(FavouriteSyncState.PENDING_ADD.name, stored?.syncState)
        }

    // unmarkFavourite

    @Test
    fun `given api succeeds when unmarkFavourite is called then removes entity and returns Success`() = runTest {
        // Given
        val (repo, dao, _) = createRepository()
        dao.upsert(fakeGuide.toFavouriteGuideEntityForTest(FavouriteSyncState.SYNCED))

        // When
        val result = repo.unmark(fakeGuide.id)

        // Then
        assertIs<Result.Success<Unit>>(result)
        assertEquals(null, dao.getByGuideId(fakeGuide.id))
    }

    @Test
    fun `given api throws when unmarkFavourite is called then still returns Success with entity left PENDING_REMOVE`() =
        runTest {
            // Given
            val (repo, dao, _) = createRepository(api = FakeFavouritesGuidesApi(shouldThrow = true))
            dao.upsert(fakeGuide.toFavouriteGuideEntityForTest(FavouriteSyncState.SYNCED))

            // When
            val result = repo.unmark(fakeGuide.id)

            // Then
            assertIs<Result.Success<Unit>>(result)
            val stored = dao.getByGuideId(fakeGuide.id)
            assertEquals(FavouriteSyncState.PENDING_REMOVE.name, stored?.syncState)
        }

    // getFavouritesPagingFlow

    @Test
    fun `given getFavouritesPagingFlow is called then returns a non-null Pager-backed flow`() = runTest {
        // Given
        val (repo, _, _) = createRepository()

        // When
        val flow = repo.getFavouritesPagingFlow()

        // Then
        assertNotNull(flow)
    }

    // syncFavourites

    @Test
    fun `given changes available when syncFavourites is called then advances syncedAt`() = runTest {
        // Given
        val (repo, _, settings) = createRepository()
        settings.saveLastFavouritesSyncAttemptAt(0L)

        // When
        val result = repo.sync()

        // Then
        assertIs<Result.Success<Unit>>(result)
        assertEquals("2026-08-01T10:30:00Z", settings.getLastFavouritesSyncedAt())
    }

    @Test
    fun `given remote removed ids when syncFavourites is called then deletes them locally`() = runTest {
        // Given
        val dao = FakeFavouriteDao(
            initialEntities = listOf(fakeGuide.toFavouriteGuideEntityForTest(FavouriteSyncState.SYNCED)),
        )
        val api = FakeFavouritesGuidesApi()
        api.fakeSyncResponse = api.fakeSyncResponse.copy(removedIds = listOf(fakeGuide.id))
        val (repo, _, settings) = createRepository(api = api, dao = dao)
        settings.saveLastFavouritesSyncAttemptAt(0L)

        // When
        val result = repo.sync()

        // Then
        assertIs<Result.Success<Unit>>(result)
        assertEquals(null, dao.getByGuideId(fakeGuide.id))
    }

    @Test
    fun `given DAO write throws when syncFavourites is called then does not advance syncedAt`() = runTest {
        // Given
        val dao = FakeFavouriteDao(
            shouldThrowOnWrite = true,
            initialEntities = listOf(fakeGuide.toFavouriteGuideEntityForTest(FavouriteSyncState.SYNCED)),
        )
        val api = FakeFavouritesGuidesApi()
        api.fakeSyncResponse = api.fakeSyncResponse.copy(removedIds = listOf(fakeGuide.id))
        val (repo, _, settings) = createRepository(api = api, dao = dao)
        settings.saveLastFavouritesSyncAttemptAt(0L)

        // When
        val result = repo.sync()

        // Then
        assertIs<Result.Failure>(result)
        assertEquals(null, settings.getLastFavouritesSyncedAt())
    }

    @Test
    fun `given last sync attempt was recent when syncFavourites is called then returns early without calling API`() =
        runTest {
            // Given
            val (repo, _, settings) = createRepository(api = FakeFavouritesGuidesApi(shouldThrow = true))
            settings.saveLastFavouritesSyncAttemptAt(now())

            // When
            val result = repo.sync()

            // Then
            assertIs<Result.Success<Unit>>(result)
            assertEquals(null, settings.getLastFavouritesSyncedAt())
        }

    @Test
    fun `given last sync attempt was long ago when syncFavourites is called then proceeds`() = runTest {
        // Given
        val (repo, _, settings) = createRepository()
        settings.saveLastFavouritesSyncAttemptAt(0L)

        // When
        val result = repo.sync()

        // Then
        assertIs<Result.Success<Unit>>(result)
        assertEquals("2026-08-01T10:30:00Z", settings.getLastFavouritesSyncedAt())
    }
}

@OptIn(ExperimentalTime::class)
private fun Guide.toFavouriteGuideEntityForTest(syncState: FavouriteSyncState) = this.toFavouriteGuideEntity(
    favouritedAt = Clock.System.now().toEpochMilliseconds(),
    syncState = syncState,
    position = 0,
)
