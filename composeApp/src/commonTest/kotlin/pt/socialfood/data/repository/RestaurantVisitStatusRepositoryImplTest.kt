package pt.socialfood.data.repository

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.data.local.entity.SyncState
import pt.socialfood.data.network.model.restaurantvisitstatus.RestaurantVisitStatusSyncResponse
import pt.socialfood.data.paging.RestaurantVisitStatusCacheTransactionRunner
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.fakes.FakeRestaurantVisitStatusApi
import pt.socialfood.fakes.FakeRestaurantVisitStatusDao
import pt.socialfood.fakes.FakeRestaurantVisitStatusRemoteKeyDao
import pt.socialfood.fakes.FakeSettingsRepository
import pt.socialfood.mapper.toRestaurantVisitStatusEntity
import pt.socialfood.random.nextEnum
import pt.socialfood.random.nextRestaurant
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class RestaurantVisitStatusRepositoryImplTest {
    private val fakeRestaurant = Random.nextRestaurant()

    @OptIn(ExperimentalTime::class)
    private fun now(): Long = Clock.System.now().toEpochMilliseconds()

    private fun createRepository(
        api: FakeRestaurantVisitStatusApi = FakeRestaurantVisitStatusApi(),
        dao: FakeRestaurantVisitStatusDao = FakeRestaurantVisitStatusDao(),
        settings: FakeSettingsRepository = FakeSettingsRepository(),
    ): Triple<RestaurantVisitStatusRepositoryImpl, FakeRestaurantVisitStatusDao, FakeSettingsRepository> = Triple(
        RestaurantVisitStatusRepositoryImpl(
            restaurantVisitStatusApi = api,
            restaurantVisitStatusDao = dao,
            restaurantVisitStatusRemoteKeyDao = FakeRestaurantVisitStatusRemoteKeyDao(),
            transactionRunner = RestaurantVisitStatusCacheTransactionRunner { it() },
            settingsRepository = settings,
        ),
        dao,
        settings,
    )

    // mark

    @Test
    fun `given api succeeds when mark is called then persists SYNCED entity and returns Success`() = runTest {
        // Given
        val status = Random.nextEnum<VisitStatus>()
        val (repo, dao, _) = createRepository()

        // When
        val result = repo.mark(fakeRestaurant, status)

        // Then
        assertIs<Result.Success<Unit>>(result)
        val stored = dao.getByRestaurantId(fakeRestaurant.id)
        assertEquals(SyncState.SYNCED.name, stored?.syncState)
        assertEquals(status.name, stored?.status)
    }

    @Test
    fun `given api throws when mark is called then still returns Success with entity left PENDING_ADD`() = runTest {
        // Given
        val status = Random.nextEnum<VisitStatus>()
        val (repo, dao, _) = createRepository(api = FakeRestaurantVisitStatusApi(shouldThrow = true))

        // When
        val result = repo.mark(fakeRestaurant, status)

        // Then
        assertIs<Result.Success<Unit>>(result)
        val stored = dao.getByRestaurantId(fakeRestaurant.id)
        assertEquals(SyncState.PENDING_ADD.name, stored?.syncState)
    }

    // unmark

    @Test
    fun `given api succeeds when unmark is called then removes entity and returns Success`() = runTest {
        // Given
        val status = Random.nextEnum<VisitStatus>()
        val (repo, dao, _) = createRepository()
        dao.upsert(fakeRestaurant.toRestaurantVisitEntityForTest(status, SyncState.SYNCED))

        // When
        val result = repo.unmark(fakeRestaurant.id, status)

        // Then
        assertIs<Result.Success<Unit>>(result)
        assertEquals(null, dao.getByRestaurantId(fakeRestaurant.id))
    }

    @Test
    fun `given api throws when unmark is called then still returns Success with entity left PENDING_REMOVE`() =
        runTest {
            // Given
            val status = Random.nextEnum<VisitStatus>()
            val (repo, dao, _) = createRepository(api = FakeRestaurantVisitStatusApi(shouldThrow = true))
            dao.upsert(fakeRestaurant.toRestaurantVisitEntityForTest(status, SyncState.SYNCED))

            // When
            val result = repo.unmark(fakeRestaurant.id, status)

            // Then
            assertIs<Result.Success<Unit>>(result)
            val stored = dao.getByRestaurantId(fakeRestaurant.id)
            assertEquals(SyncState.PENDING_REMOVE.name, stored?.syncState)
        }

    // getStatus

    @Test
    fun `given a stored visit when getStatus is called then returns its status`() = runTest {
        // Given
        val status = Random.nextEnum<VisitStatus>()
        val (repo, dao, _) = createRepository()
        dao.upsert(fakeRestaurant.toRestaurantVisitEntityForTest(status, SyncState.SYNCED))

        // When
        val result = repo.getStatus(fakeRestaurant.id)

        // Then
        assertIs<Result.Success<VisitStatus?>>(result)
        assertEquals(status, result.data)
    }

    @Test
    fun `given no stored visit when getStatus is called then returns null`() = runTest {
        // Given
        val (repo, _, _) = createRepository()

        // When
        val result = repo.getStatus(fakeRestaurant.id)

        // Then
        assertIs<Result.Success<VisitStatus?>>(result)
        assertEquals(null, result.data)
    }

    @Test
    fun `given a visit stuck PENDING_REMOVE when getStatus is called then returns null`() = runTest {
        // Given
        val status = Random.nextEnum<VisitStatus>()
        val (repo, dao, _) = createRepository()
        dao.upsert(fakeRestaurant.toRestaurantVisitEntityForTest(status, SyncState.PENDING_REMOVE))

        // When
        val result = repo.getStatus(fakeRestaurant.id)

        // Then
        assertIs<Result.Success<VisitStatus?>>(result)
        assertEquals(null, result.data)
    }

    // getPagingFlow

    @Test
    fun `given getPagingFlow is called then returns a non-null Pager-backed flow for the status`() = runTest {
        // Given
        val (repo, _, _) = createRepository()

        // When
        val wishlistFlow = repo.getPagingFlow(VisitStatus.WISHLIST)
        val visitedFlow = repo.getPagingFlow(VisitStatus.VISITED)

        // Then
        assertNotNull(wishlistFlow)
        assertNotNull(visitedFlow)
    }

    // sync

    @Test
    fun `given changes available when sync is called then advances syncedAt`() = runTest {
        // Given
        val status = Random.nextEnum<VisitStatus>()
        val api = FakeRestaurantVisitStatusApi()
        val restaurantId = api.fakeRestaurants.items.first().id
        api.fakeSyncResponse = api.fakeSyncResponse.copy(
            updated = listOf(RestaurantVisitStatusSyncResponse.RestaurantStatusEntry(restaurantId, status.name)),
        )
        val (repo, _, settings) = createRepository(api = api)
        settings.saveLastRestaurantVisitStatusSyncAttemptAt(0L)

        // When
        val result = repo.sync()

        // Then
        assertIs<Result.Success<Unit>>(result)
        assertEquals("2026-08-01T10:30:00Z", settings.getLastRestaurantVisitStatusSyncedAt())
    }

    @Test
    fun `given pending adds when sync is called then marks them SYNCED`() = runTest {
        // Given
        val status = Random.nextEnum<VisitStatus>()
        val dao = FakeRestaurantVisitStatusDao(
            initialEntities = listOf(fakeRestaurant.toRestaurantVisitEntityForTest(status, SyncState.PENDING_ADD)),
        )
        val (repo, _, settings) = createRepository(dao = dao)
        settings.saveLastRestaurantVisitStatusSyncAttemptAt(0L)

        // When
        val result = repo.sync()

        // Then
        assertIs<Result.Success<Unit>>(result)
        assertEquals(SyncState.SYNCED.name, dao.getByRestaurantId(fakeRestaurant.id)?.syncState)
    }

    @Test
    fun `given pending removes when sync is called then deletes them`() = runTest {
        // Given
        val status = Random.nextEnum<VisitStatus>()
        val dao = FakeRestaurantVisitStatusDao(
            initialEntities = listOf(fakeRestaurant.toRestaurantVisitEntityForTest(status, SyncState.PENDING_REMOVE)),
        )
        val (repo, _, settings) = createRepository(dao = dao)
        settings.saveLastRestaurantVisitStatusSyncAttemptAt(0L)

        // When
        val result = repo.sync()

        // Then
        assertIs<Result.Success<Unit>>(result)
        assertEquals(null, dao.getByRestaurantId(fakeRestaurant.id))
    }

    @Test
    fun `given a pending row fails to push when sync is called then still succeeds and advances syncedAt`() = runTest {
        // Given
        val status = Random.nextEnum<VisitStatus>()
        val dao = FakeRestaurantVisitStatusDao(
            shouldThrowOnWrite = true,
            initialEntities = listOf(fakeRestaurant.toRestaurantVisitEntityForTest(status, SyncState.PENDING_ADD)),
        )
        val (repo, _, settings) = createRepository(dao = dao)
        settings.saveLastRestaurantVisitStatusSyncAttemptAt(0L)

        // When
        val result = repo.sync()

        // Then
        assertIs<Result.Success<Unit>>(result)
        assertEquals("2026-08-01T10:30:00Z", settings.getLastRestaurantVisitStatusSyncedAt())
    }

    @Test
    fun `given the sync api call fails when sync is called then returns Failure and does not advance syncedAt`() =
        runTest {
            // Given
            val (repo, _, settings) = createRepository(api = FakeRestaurantVisitStatusApi(shouldThrow = true))
            settings.saveLastRestaurantVisitStatusSyncAttemptAt(0L)

            // When
            val result = repo.sync()

            // Then
            assertIs<Result.Failure>(result)
            assertEquals(null, settings.getLastRestaurantVisitStatusSyncedAt())
        }

    @Test
    fun `given last sync attempt was recent when sync is called then returns early without calling the API`() =
        runTest {
            // Given
            val (repo, _, settings) = createRepository(api = FakeRestaurantVisitStatusApi(shouldThrow = true))
            settings.saveLastRestaurantVisitStatusSyncAttemptAt(now())

            // When
            val result = repo.sync()

            // Then
            assertIs<Result.Success<Unit>>(result)
            assertEquals(null, settings.getLastRestaurantVisitStatusSyncedAt())
        }

    @Test
    fun `given last sync attempt was long ago when sync is called then proceeds`() = runTest {
        // Given
        val (repo, _, settings) = createRepository()
        settings.saveLastRestaurantVisitStatusSyncAttemptAt(0L)

        // When
        val result = repo.sync()

        // Then
        assertIs<Result.Success<Unit>>(result)
        assertEquals("2026-08-01T10:30:00Z", settings.getLastRestaurantVisitStatusSyncedAt())
    }
}

@OptIn(ExperimentalTime::class)
private fun Restaurant.toRestaurantVisitEntityForTest(status: VisitStatus, syncState: SyncState) =
    this.toRestaurantVisitStatusEntity(
        status = status,
        recordedAt = Clock.System.now().toEpochMilliseconds(),
        syncState = syncState,
        position = 0,
    )
