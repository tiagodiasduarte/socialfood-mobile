package pt.socialfood.data.repository

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.data.local.entity.SyncState
import pt.socialfood.domain.model.PagedRestaurantVisitStatus
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.fakes.FakeRestaurantVisitStatusApi
import pt.socialfood.fakes.FakeRestaurantVisitStatusDao
import pt.socialfood.fakes.FakeSettingsRepository
import pt.socialfood.mapper.toRestaurantVisitStatusEntity
import pt.socialfood.random.nextEnum
import pt.socialfood.random.nextRestaurant
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
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
    ): Triple<RestaurantVisitStatusRepositoryImpl, FakeRestaurantVisitStatusDao, FakeSettingsRepository> =
        Triple(RestaurantVisitStatusRepositoryImpl(api, dao, settings), dao, settings)

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

    // getPaged

    @Test
    fun `given cached visits when getPaged is called then reads from DAO only and never calls the API`() = runTest {
        // Given
        val status = Random.nextEnum<VisitStatus>()
        val (repo, dao, _) = createRepository(api = FakeRestaurantVisitStatusApi(shouldThrow = true))
        dao.upsert(fakeRestaurant.toRestaurantVisitEntityForTest(status, SyncState.SYNCED))

        // When
        val result = repo.getPaged(status = status, page = 1, limit = 10)

        // Then
        assertIs<Result.Success<PagedRestaurantVisitStatus>>(result)
        assertEquals(1, result.data.visits.size)
        assertEquals(
            fakeRestaurant.id,
            result.data.visits
                .first()
                .restaurant.id,
        )
    }

    @Test
    fun `given visits with a different status when getPaged is called then excludes them`() = runTest {
        // Given
        val (repo, dao, _) = createRepository()
        dao.upsert(
            fakeRestaurant.toRestaurantVisitEntityForTest(VisitStatus.WISHLIST, SyncState.SYNCED),
        )

        // When
        val result = repo.getPaged(status = VisitStatus.VISITED, page = 1, limit = 10)

        // Then
        assertIs<Result.Success<PagedRestaurantVisitStatus>>(result)
        assertTrue(result.data.visits.isEmpty())
    }

    @Test
    fun `given a visit stuck PENDING_REMOVE when getPaged is called then excludes it`() = runTest {
        // Given
        val status = Random.nextEnum<VisitStatus>()
        val (repo, dao, _) = createRepository()
        dao.upsert(fakeRestaurant.toRestaurantVisitEntityForTest(status, SyncState.PENDING_REMOVE))

        // When
        val result = repo.getPaged(status = status, page = 1, limit = 10)

        // Then
        assertIs<Result.Success<PagedRestaurantVisitStatus>>(result)
        assertTrue(result.data.visits.isEmpty())
        assertEquals(0, result.data.total)
    }

    // sync

    @Test
    fun `given changes available when sync is called then applies them and advances syncedAt`() = runTest {
        // Given
        val status = Random.nextEnum<VisitStatus>()
        val (repo, dao, settings) = createRepository()
        settings.saveLastRestaurantVisitStatusSyncAttemptAt(0L)

        // When
        val result = repo.sync(status)

        // Then
        assertIs<Result.Success<Unit>>(result)
        assertEquals("2026-08-01T10:30:00Z", settings.getLastRestaurantVisitStatusSyncedAt())
        assertTrue(dao.getPaged(status = status.name, limit = 10, offset = 0).isNotEmpty())
    }

    @Test
    fun `given a restaurant exists as VISITED when sync reports it as WISHLIST then changes its state`() = runTest {
        // Given
        val api = FakeRestaurantVisitStatusApi()
        val (repo, dao, settings) = createRepository(api = api)
        val existingId = api.fakeRestaurants.items.first().id
        val existingRestaurant = Random.nextRestaurant(id = existingId)
        dao.upsert(existingRestaurant.toRestaurantVisitEntityForTest(VisitStatus.VISITED, SyncState.SYNCED))
        settings.saveLastRestaurantVisitStatusSyncAttemptAt(0L)

        // When
        val result = repo.sync(VisitStatus.WISHLIST)

        // Then
        assertIs<Result.Success<Unit>>(result)
        val stored = dao.getByRestaurantId(existingId)
        assertEquals(VisitStatus.WISHLIST.name, stored?.status)
        assertEquals(1, dao.getPaged(status = VisitStatus.WISHLIST.name, limit = 10, offset = 0).size)
        assertTrue(dao.getPaged(status = VisitStatus.VISITED.name, limit = 10, offset = 0).isEmpty())
    }

    @Test
    fun `given DAO write throws when sync is called then does not advance syncedAt`() = runTest {
        // Given
        val status = Random.nextEnum<VisitStatus>()
        val (repo, _, settings) = createRepository(dao = FakeRestaurantVisitStatusDao(shouldThrowOnWrite = true))
        settings.saveLastRestaurantVisitStatusSyncAttemptAt(0L)

        // When
        val result = repo.sync(status)

        // Then
        assertIs<Result.Failure>(result)
        assertEquals(null, settings.getLastRestaurantVisitStatusSyncedAt())
    }

    @Test
    @Suppress("MaxLineLength", "ktlint:standard:max-line-length")
    fun `given last sync attempt was recent when sync is called then returns early without calling the API`() =
        runTest {
            // Given
            val status = Random.nextEnum<VisitStatus>()
            val (repo, _, settings) = createRepository(api = FakeRestaurantVisitStatusApi(shouldThrow = true))
            settings.saveLastRestaurantVisitStatusSyncAttemptAt(now())

            // When
            val result = repo.sync(status)

            // Then
            assertIs<Result.Success<Unit>>(result)
            assertEquals(null, settings.getLastRestaurantVisitStatusSyncedAt())
        }

    @Test
    fun `given last sync attempt was long ago when sync is called then proceeds`() = runTest {
        // Given
        val status = Random.nextEnum<VisitStatus>()
        val (repo, _, settings) = createRepository()
        settings.saveLastRestaurantVisitStatusSyncAttemptAt(0L)

        // When
        val result = repo.sync(status)

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
    )
