package pt.socialfood.data.repository

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.data.local.entity.FavouriteSyncState
import pt.socialfood.data.paging.FavouriteRestaurantCacheTransactionRunner
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.fakes.FakeFavouriteRestaurantDao
import pt.socialfood.fakes.FakeFavouriteRestaurantRemoteKeyDao
import pt.socialfood.fakes.FakeFavouriteRestaurantsApi
import pt.socialfood.fakes.FakeSettingsRepository
import pt.socialfood.mapper.toFavouriteRestaurantEntity
import pt.socialfood.random.nextRestaurant
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class FavouriteRestaurantsRepositoryImplTest {
    private val fakeRestaurant = Random.nextRestaurant()

    @OptIn(ExperimentalTime::class)
    private fun now(): Long = Clock.System.now().toEpochMilliseconds()

    private fun createRepository(
        api: FakeFavouriteRestaurantsApi = FakeFavouriteRestaurantsApi(),
        dao: FakeFavouriteRestaurantDao = FakeFavouriteRestaurantDao(),
        settings: FakeSettingsRepository = FakeSettingsRepository(),
    ): Triple<FavouriteRestaurantsRepositoryImpl, FakeFavouriteRestaurantDao, FakeSettingsRepository> = Triple(
        FavouriteRestaurantsRepositoryImpl(
            favouriteRestaurantsApi = api,
            favouriteRestaurantDao = dao,
            favouriteRestaurantRemoteKeyDao = FakeFavouriteRestaurantRemoteKeyDao(),
            transactionRunner = FavouriteRestaurantCacheTransactionRunner { it() },
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
        val result = repo.markFavourite(fakeRestaurant)

        // Then
        assertIs<Result.Success<Unit>>(result)
        val stored = dao.getByRestaurantId(fakeRestaurant.id)
        assertEquals(FavouriteSyncState.SYNCED.name, stored?.syncState)
    }

    @Test
    fun `given api throws when markFavourite is called then still returns Success with entity left PENDING_ADD`() =
        runTest {
            // Given
            val (repo, dao, _) = createRepository(api = FakeFavouriteRestaurantsApi(shouldThrow = true))

            // When
            val result = repo.markFavourite(fakeRestaurant)

            // Then
            assertIs<Result.Success<Unit>>(result)
            val stored = dao.getByRestaurantId(fakeRestaurant.id)
            assertEquals(FavouriteSyncState.PENDING_ADD.name, stored?.syncState)
        }

    // unmarkFavourite

    @Test
    fun `given api succeeds when unmarkFavourite is called then removes entity and returns Success`() = runTest {
        // Given
        val (repo, dao, _) = createRepository()
        dao.upsert(fakeRestaurant.toFavouriteRestaurantEntityForTest(FavouriteSyncState.SYNCED))

        // When
        val result = repo.unmarkFavourite(fakeRestaurant.id)

        // Then
        assertIs<Result.Success<Unit>>(result)
        assertEquals(null, dao.getByRestaurantId(fakeRestaurant.id))
    }

    @Test
    fun `given api throws when unmarkFavourite is called then still returns Success with entity left PENDING_REMOVE`() =
        runTest {
            // Given
            val (repo, dao, _) = createRepository(api = FakeFavouriteRestaurantsApi(shouldThrow = true))
            dao.upsert(fakeRestaurant.toFavouriteRestaurantEntityForTest(FavouriteSyncState.SYNCED))

            // When
            val result = repo.unmarkFavourite(fakeRestaurant.id)

            // Then
            assertIs<Result.Success<Unit>>(result)
            val stored = dao.getByRestaurantId(fakeRestaurant.id)
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
    fun `given changes available when syncFavourites is called then applies them and advances syncedAt`() = runTest {
        // Given
        val (repo, dao, settings) = createRepository()
        settings.saveLastFavouriteRestaurantsSyncAttemptAt(0L)

        // When
        val result = repo.syncFavourites()

        // Then
        assertIs<Result.Success<Unit>>(result)
        assertEquals("2026-08-01T10:30:00Z", settings.getLastFavouriteRestaurantsSyncedAt())
        assertNotNull(dao.getByRestaurantId("restaurant-id"))
    }

    @Test
    fun `given remote added restaurant when syncFavourites is called then hydrates it from the response`() = runTest {
        // Given
        val api = FakeFavouriteRestaurantsApi()
        val addedRestaurant = api.fakeSyncResponse.added.first()
        val (repo, dao, settings) = createRepository(api = api)
        settings.saveLastFavouriteRestaurantsSyncAttemptAt(0L)

        // When
        val result = repo.syncFavourites()

        // Then
        assertIs<Result.Success<Unit>>(result)
        assertEquals(0, api.findFavouriteRestaurantsCallCount)
        val stored = dao.getByRestaurantId(addedRestaurant.id)
        assertNotNull(stored)
        assertEquals(FavouriteSyncState.SYNCED.name, stored.syncState)
    }

    @Test
    fun `given remote removed ids when syncFavourites is called then deletes them locally`() = runTest {
        // Given
        val dao = FakeFavouriteRestaurantDao(
            initialEntities = listOf(fakeRestaurant.toFavouriteRestaurantEntityForTest(FavouriteSyncState.SYNCED)),
        )
        val api = FakeFavouriteRestaurantsApi()
        api.fakeSyncResponse = api.fakeSyncResponse.copy(removedIds = listOf(fakeRestaurant.id))
        val (repo, _, settings) = createRepository(api = api, dao = dao)
        settings.saveLastFavouriteRestaurantsSyncAttemptAt(0L)

        // When
        val result = repo.syncFavourites()

        // Then
        assertIs<Result.Success<Unit>>(result)
        assertEquals(null, dao.getByRestaurantId(fakeRestaurant.id))
    }

    @Test
    fun `given DAO write throws when syncFavourites is called then does not advance syncedAt`() = runTest {
        // Given
        val dao = FakeFavouriteRestaurantDao(
            shouldThrowOnWrite = true,
            initialEntities = listOf(fakeRestaurant.toFavouriteRestaurantEntityForTest(FavouriteSyncState.SYNCED)),
        )
        val api = FakeFavouriteRestaurantsApi()
        api.fakeSyncResponse = api.fakeSyncResponse.copy(removedIds = listOf(fakeRestaurant.id))
        val (repo, _, settings) = createRepository(api = api, dao = dao)
        settings.saveLastFavouriteRestaurantsSyncAttemptAt(0L)

        // When
        val result = repo.syncFavourites()

        // Then
        assertIs<Result.Failure>(result)
        assertEquals(null, settings.getLastFavouriteRestaurantsSyncedAt())
    }

    @Test
    fun `given last sync attempt was recent when syncFavourites is called then returns early without calling API`() =
        runTest {
            // Given
            val (repo, _, settings) = createRepository(api = FakeFavouriteRestaurantsApi(shouldThrow = true))
            settings.saveLastFavouriteRestaurantsSyncAttemptAt(now())

            // When
            val result = repo.syncFavourites()

            // Then
            assertIs<Result.Success<Unit>>(result)
            assertEquals(null, settings.getLastFavouriteRestaurantsSyncedAt())
        }

    @Test
    fun `given last sync attempt was long ago when syncFavourites is called then proceeds`() = runTest {
        // Given
        val (repo, _, settings) = createRepository()
        settings.saveLastFavouriteRestaurantsSyncAttemptAt(0L)

        // When
        val result = repo.syncFavourites()

        // Then
        assertIs<Result.Success<Unit>>(result)
        assertEquals("2026-08-01T10:30:00Z", settings.getLastFavouriteRestaurantsSyncedAt())
    }
}

@OptIn(ExperimentalTime::class)
private fun Restaurant.toFavouriteRestaurantEntityForTest(syncState: FavouriteSyncState) =
    this.toFavouriteRestaurantEntity(
        favouritedAt = Clock.System.now().toEpochMilliseconds(),
        syncState = syncState,
        position = 0,
    )
