package pt.socialfood.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.paging.testing.TestPager
import kotlinx.coroutines.test.runTest
import pt.socialfood.data.local.entity.RestaurantVisitStatusEntity
import pt.socialfood.data.local.entity.RestaurantVisitStatusRemoteKeyEntity
import pt.socialfood.data.local.entity.SyncState
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.fakes.FakeRestaurantVisitStatusApi
import pt.socialfood.fakes.FakeRestaurantVisitStatusDao
import pt.socialfood.fakes.FakeRestaurantVisitStatusRemoteKeyDao
import pt.socialfood.mapper.toRestaurant
import pt.socialfood.mapper.toRestaurantVisitStatusEntity
import pt.socialfood.random.nextRestaurantResponse
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

private val STATUS = VisitStatus.WISHLIST
private val SCOPE = STATUS.name

@OptIn(ExperimentalPagingApi::class)
class RestaurantVisitStatusRemoteMediatorTest {
    private val config = PagingConfig(pageSize = 10)
    private val emptyState =
        PagingState<Int, RestaurantVisitStatusEntity>(
            pages = emptyList(),
            anchorPosition = null,
            config = config,
            leadingPlaceholderCount = 0,
        )

    private fun entity(id: String, status: VisitStatus = STATUS, position: Int = 0) =
        Random.nextRestaurantResponse(id = id).toRestaurant().toRestaurantVisitStatusEntity(
            status = status,
            recordedAt = 0L,
            syncState = SyncState.SYNCED,
            position = position,
        )

    private fun createMediator(
        api: FakeRestaurantVisitStatusApi,
        dao: FakeRestaurantVisitStatusDao,
        remoteKeyDao: FakeRestaurantVisitStatusRemoteKeyDao,
        status: VisitStatus = STATUS,
    ) = RestaurantVisitStatusRemoteMediator(
        status = status,
        restaurantVisitStatusApi = api,
        restaurantVisitStatusDao = dao,
        remoteKeyDao = remoteKeyDao,
        transactionRunner = RestaurantVisitStatusCacheTransactionRunner { it() },
    )

    @Test
    fun `given empty cache when REFRESH runs then fetches page 1 and upserts into dao for the status`() = runTest {
        // Given
        val api = FakeRestaurantVisitStatusApi()
        api.fakeRestaurants = api.fakeRestaurants.copy(
            items = listOf(Random.nextRestaurantResponse(id = "r1"), Random.nextRestaurantResponse(id = "r2")),
            total = 2,
        )
        val dao = FakeRestaurantVisitStatusDao()
        val remoteKeyDao = FakeRestaurantVisitStatusRemoteKeyDao()
        val mediator = createMediator(api, dao, remoteKeyDao)

        // When
        val result = mediator.load(LoadType.REFRESH, emptyState)

        // Then
        assertIs<RemoteMediator.MediatorResult.Success>(result)
        assertEquals(1, api.findCallCount)
        assertEquals(1, api.lastFindPage)
        assertEquals(STATUS, api.lastFindStatus)
        val cached = dao.getAll()
        assertEquals(listOf("r1", "r2"), cached.map { it.restaurantId })
        assertTrue(cached.all { it.status == STATUS.name })
    }

    @Test
    fun `given REFRESH succeeds when load completes then old rows and remote key for status are replaced not merged`() =
        runTest {
            // Given
            val dao = FakeRestaurantVisitStatusDao()
            val remoteKeyDao = FakeRestaurantVisitStatusRemoteKeyDao()
            dao.upsertAll(listOf(entity(id = "old-id")))
            remoteKeyDao.upsert(
                RestaurantVisitStatusRemoteKeyEntity(scope = SCOPE, nextPage = 2, endOfPaginationReached = false),
            )
            val api = FakeRestaurantVisitStatusApi()
            api.fakeRestaurants = api.fakeRestaurants.copy(
                items = listOf(Random.nextRestaurantResponse(id = "new-id")),
                total = 1,
            )
            val mediator = createMediator(api, dao, remoteKeyDao)

            // When
            val result = mediator.load(LoadType.REFRESH, emptyState)

            // Then
            assertIs<RemoteMediator.MediatorResult.Success>(result)
            val cached = dao.getAll()
            assertEquals(listOf("new-id"), cached.map { it.restaurantId })
            val remoteKey = remoteKeyDao.getByScope(SCOPE)
            assertNull(remoteKey?.nextPage)
            assertEquals(true, remoteKey?.endOfPaginationReached)
        }

    @Test
    fun `given cache has a next page key when APPEND load is triggered then fetches that page and appends it`() =
        runTest {
            // Given
            val dao = FakeRestaurantVisitStatusDao()
            val remoteKeyDao = FakeRestaurantVisitStatusRemoteKeyDao()
            dao.upsertAll(listOf(entity(id = "r1", position = 0)))
            remoteKeyDao.upsert(
                RestaurantVisitStatusRemoteKeyEntity(scope = SCOPE, nextPage = 2, endOfPaginationReached = false),
            )
            val api = FakeRestaurantVisitStatusApi()
            api.fakeRestaurants =
                api.fakeRestaurants.copy(items = listOf(Random.nextRestaurantResponse(id = "r2")), total = 11)
            val mediator = createMediator(api, dao, remoteKeyDao)

            // When
            val result = mediator.load(LoadType.APPEND, emptyState)

            // Then
            assertIs<RemoteMediator.MediatorResult.Success>(result)
            assertEquals(2, api.lastFindPage)
            val cached = dao.getAll()
            assertEquals(listOf("r1", "r2"), cached.map { it.restaurantId })
        }

    @Test
    fun `given no next page key when APPEND runs then returns Success endOfPaginationReached without api call`() =
        runTest {
            // Given
            val dao = FakeRestaurantVisitStatusDao()
            val remoteKeyDao = FakeRestaurantVisitStatusRemoteKeyDao()
            remoteKeyDao.upsert(
                RestaurantVisitStatusRemoteKeyEntity(scope = SCOPE, nextPage = null, endOfPaginationReached = true),
            )
            val api = FakeRestaurantVisitStatusApi()
            val mediator = createMediator(api, dao, remoteKeyDao)

            // When
            val result = mediator.load(LoadType.APPEND, emptyState)

            // Then
            assertIs<RemoteMediator.MediatorResult.Success>(result)
            assertTrue(result.endOfPaginationReached)
            assertEquals(0, api.findCallCount)
        }

    @Test
    fun `given page times limit reaches total when loaded then pagination ends and remote key reflects it`() = runTest {
        // Given
        val api = FakeRestaurantVisitStatusApi()
        api.fakeRestaurants =
            api.fakeRestaurants.copy(items = listOf(Random.nextRestaurantResponse(id = "r1")), total = 1)
        val dao = FakeRestaurantVisitStatusDao()
        val remoteKeyDao = FakeRestaurantVisitStatusRemoteKeyDao()
        val mediator = createMediator(api, dao, remoteKeyDao)

        // When
        val result = mediator.load(LoadType.REFRESH, emptyState)

        // Then
        assertIs<RemoteMediator.MediatorResult.Success>(result)
        assertTrue(result.endOfPaginationReached)
        val remoteKey = remoteKeyDao.getByScope(SCOPE)
        assertEquals(true, remoteKey?.endOfPaginationReached)
        assertNull(remoteKey?.nextPage)
    }

    @Test
    fun `given api throws when load is triggered then returns Error without clearing the existing cache`() = runTest {
        // Given
        val dao = FakeRestaurantVisitStatusDao()
        val remoteKeyDao = FakeRestaurantVisitStatusRemoteKeyDao()
        dao.upsertAll(listOf(entity(id = "existing-id")))
        val api = FakeRestaurantVisitStatusApi(shouldThrow = true)
        val mediator = createMediator(api, dao, remoteKeyDao)

        // When
        val result = mediator.load(LoadType.REFRESH, emptyState)

        // Then
        assertIs<RemoteMediator.MediatorResult.Error>(result)
        assertEquals(listOf("existing-id"), dao.getAll().map { it.restaurantId })
    }

    @Test
    fun `given PREPEND load then returns Success endOfPaginationReached true without calling api`() = runTest {
        // Given
        val api = FakeRestaurantVisitStatusApi()
        val dao = FakeRestaurantVisitStatusDao()
        val remoteKeyDao = FakeRestaurantVisitStatusRemoteKeyDao()
        val mediator = createMediator(api, dao, remoteKeyDao)

        // When
        val result = mediator.load(LoadType.PREPEND, emptyState)

        // Then
        assertIs<RemoteMediator.MediatorResult.Success>(result)
        assertTrue(result.endOfPaginationReached)
        assertEquals(0, api.findCallCount)
    }

    @Test
    fun `given cache populated after REFRESH when read via TestPager then PagingSource surfaces cached rows`() =
        runTest {
            // Given
            val api = FakeRestaurantVisitStatusApi()
            api.fakeRestaurants = api.fakeRestaurants.copy(
                items = listOf(Random.nextRestaurantResponse(id = "r1"), Random.nextRestaurantResponse(id = "r2")),
                total = 2,
            )
            val dao = FakeRestaurantVisitStatusDao()
            val remoteKeyDao = FakeRestaurantVisitStatusRemoteKeyDao()
            val mediator = createMediator(api, dao, remoteKeyDao)

            // When
            mediator.load(LoadType.REFRESH, emptyState)
            val testPager = TestPager(config, dao.pagingSource(SCOPE))
            val loadResult = testPager.refresh()

            // Then
            assertIs<PagingSource.LoadResult.Page<Int, RestaurantVisitStatusEntity>>(loadResult)
            assertEquals(listOf("r1", "r2"), loadResult.data.map { it.restaurantId })
        }
}
