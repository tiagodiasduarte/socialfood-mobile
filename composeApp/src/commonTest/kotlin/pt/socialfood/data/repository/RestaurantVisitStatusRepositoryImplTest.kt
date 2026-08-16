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
            fakeRestaurant.toRestaurantVisitEntityForTest(VisitStatus.WISH, SyncState.SYNCED),
        )

        // When
        val result = repo.getPaged(status = VisitStatus.VISITED, page = 1, limit = 10)

        // Then
        assertIs<Result.Success<PagedRestaurantVisitStatus>>(result)
        assertTrue(result.data.visits.isEmpty())
    }

    // sync

    @Test
    fun `given changes available when sync is called then applies them and advances syncedAt`() = runTest {
        // Given
        val status = Random.nextEnum<VisitStatus>()
        val (repo, dao, settings) = createRepository()
        settings.saveLastRestaurantVisitStatusSyncAttemptAt(status, 0L)

        // When
        val result = repo.sync(status)

        // Then
        assertIs<Result.Success<Unit>>(result)
        assertEquals("2026-08-01T10:30:00Z", settings.getLastRestaurantVisitStatusSyncedAt(status))
        assertTrue(dao.getPaged(status = status.name, limit = 10, offset = 0).isNotEmpty())
    }

    @Test
    fun `given DAO write throws when sync is called then does not advance syncedAt`() = runTest {
        // Given
        val status = Random.nextEnum<VisitStatus>()
        val (repo, _, settings) = createRepository(dao = FakeRestaurantVisitStatusDao(shouldThrowOnWrite = true))
        settings.saveLastRestaurantVisitStatusSyncAttemptAt(status, 0L)

        // When
        val result = repo.sync(status)

        // Then
        assertIs<Result.Failure>(result)
        assertEquals(null, settings.getLastRestaurantVisitStatusSyncedAt(status))
    }

    @Test
    @Suppress("MaxLineLength", "ktlint:standard:max-line-length")
    fun `given last sync attempt was recent when sync is called then returns early without calling the API`() =
        runTest {
            // Given
            val status = Random.nextEnum<VisitStatus>()
            val (repo, _, settings) = createRepository(api = FakeRestaurantVisitStatusApi(shouldThrow = true))
            settings.saveLastRestaurantVisitStatusSyncAttemptAt(status, now())

            // When
            val result = repo.sync(status)

            // Then
            assertIs<Result.Success<Unit>>(result)
            assertEquals(null, settings.getLastRestaurantVisitStatusSyncedAt(status))
        }

    @Test
    fun `given last sync attempt was long ago when sync is called then proceeds`() = runTest {
        // Given
        val status = Random.nextEnum<VisitStatus>()
        val (repo, _, settings) = createRepository()
        settings.saveLastRestaurantVisitStatusSyncAttemptAt(status, 0L)

        // When
        val result = repo.sync(status)

        // Then
        assertIs<Result.Success<Unit>>(result)
        assertEquals("2026-08-01T10:30:00Z", settings.getLastRestaurantVisitStatusSyncedAt(status))
    }

    @Test
    fun `given a status when sync is called then does not advance syncedAt for the other status`() = runTest {
        // Given
        val (repo, _, settings) = createRepository()
        settings.saveLastRestaurantVisitStatusSyncAttemptAt(VisitStatus.WISH, 0L)

        // When
        val result = repo.sync(VisitStatus.WISH)

        // Then
        assertIs<Result.Success<Unit>>(result)
        assertEquals(null, settings.getLastRestaurantVisitStatusSyncedAt(VisitStatus.VISITED))
    }
}

@OptIn(ExperimentalTime::class)
private fun Restaurant.toRestaurantVisitEntityForTest(status: VisitStatus, syncState: SyncState) =
    this.toRestaurantVisitStatusEntity(
        status = status,
        recordedAt = Clock.System.now().toEpochMilliseconds(),
        syncState = syncState,
    )
