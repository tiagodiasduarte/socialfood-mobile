package pt.socialfood.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.paging.testing.TestPager
import kotlinx.coroutines.test.runTest
import pt.socialfood.data.local.entity.FavouriteRestaurantEntity
import pt.socialfood.data.local.entity.FavouriteRestaurantRemoteKeyEntity
import pt.socialfood.data.local.entity.FavouriteSyncState
import pt.socialfood.fakes.FakeFavouriteRestaurantDao
import pt.socialfood.fakes.FakeFavouriteRestaurantRemoteKeyDao
import pt.socialfood.fakes.FakeFavouriteRestaurantsApi
import pt.socialfood.mapper.toFavouriteRestaurantEntity
import pt.socialfood.mapper.toRestaurant
import pt.socialfood.random.nextRestaurantResponse
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalPagingApi::class)
class FavouriteRestaurantRemoteMediatorTest {
    private val config = PagingConfig(pageSize = 10)
    private val emptyState =
        PagingState<Int, FavouriteRestaurantEntity>(
            pages = emptyList(),
            anchorPosition = null,
            config = config,
            leadingPlaceholderCount = 0,
        )

    private fun entity(id: String, position: Int = 0) =
        Random.nextRestaurantResponse(id = id).toRestaurant().toFavouriteRestaurantEntity(
            favouritedAt = 0L,
            syncState = FavouriteSyncState.SYNCED,
            position = position,
        )

    private fun createMediator(
        api: FakeFavouriteRestaurantsApi,
        dao: FakeFavouriteRestaurantDao,
        remoteKeyDao: FakeFavouriteRestaurantRemoteKeyDao,
    ) = FavouriteRestaurantRemoteMediator(
        favouritesApi = api,
        favouriteDao = dao,
        remoteKeyDao = remoteKeyDao,
        transactionRunner = FavouriteRestaurantCacheTransactionRunner { it() },
    )

    @Test
    fun `given empty cache when REFRESH runs then fetches page 1 and upserts into dao`() = runTest {
        // Given
        val api = FakeFavouriteRestaurantsApi()
        api.fakeFavouriteRestaurants = api.fakeFavouriteRestaurants.copy(
            items = listOf(Random.nextRestaurantResponse(id = "r1"), Random.nextRestaurantResponse(id = "r2")),
            total = 2,
        )
        val dao = FakeFavouriteRestaurantDao()
        val remoteKeyDao = FakeFavouriteRestaurantRemoteKeyDao()
        val mediator = createMediator(api, dao, remoteKeyDao)

        // When
        val result = mediator.load(LoadType.REFRESH, emptyState)

        // Then
        assertIs<RemoteMediator.MediatorResult.Success>(result)
        assertEquals(1, api.findFavouriteRestaurantsCallCount)
        assertEquals(1, api.lastFindFavouriteRestaurantsPage)
        val cached = dao.getAll()
        assertEquals(listOf("r1", "r2"), cached.map { it.restaurantId })
    }

    @Test
    fun `given REFRESH succeeds when load completes then old rows and remote key are replaced not merged`() = runTest {
        // Given
        val dao = FakeFavouriteRestaurantDao()
        val remoteKeyDao = FakeFavouriteRestaurantRemoteKeyDao()
        dao.upsertAll(listOf(entity(id = "old-id")))
        remoteKeyDao.upsert(
            FavouriteRestaurantRemoteKeyEntity(
                scope = FAVOURITE_RESTAURANTS_SCOPE,
                nextPage = 2,
                endOfPaginationReached = false,
            ),
        )
        val api = FakeFavouriteRestaurantsApi()
        api.fakeFavouriteRestaurants = api.fakeFavouriteRestaurants.copy(
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
        val remoteKey = remoteKeyDao.getByScope(FAVOURITE_RESTAURANTS_SCOPE)
        assertNull(remoteKey?.nextPage)
        assertEquals(true, remoteKey?.endOfPaginationReached)
    }

    @Test
    fun `given cache has a next page key when APPEND load is triggered then fetches that page and appends it`() =
        runTest {
            // Given
            val dao = FakeFavouriteRestaurantDao()
            val remoteKeyDao = FakeFavouriteRestaurantRemoteKeyDao()
            dao.upsertAll(listOf(entity(id = "r1", position = 0)))
            remoteKeyDao.upsert(
                FavouriteRestaurantRemoteKeyEntity(
                    scope = FAVOURITE_RESTAURANTS_SCOPE,
                    nextPage = 2,
                    endOfPaginationReached = false,
                ),
            )
            val api = FakeFavouriteRestaurantsApi()
            api.fakeFavouriteRestaurants =
                api.fakeFavouriteRestaurants.copy(items = listOf(Random.nextRestaurantResponse(id = "r2")), total = 11)
            val mediator = createMediator(api, dao, remoteKeyDao)

            // When
            val result = mediator.load(LoadType.APPEND, emptyState)

            // Then
            assertIs<RemoteMediator.MediatorResult.Success>(result)
            assertEquals(2, api.lastFindFavouriteRestaurantsPage)
            val cached = dao.getAll()
            assertEquals(listOf("r1", "r2"), cached.map { it.restaurantId })
        }

    @Test
    fun `given no next page key when APPEND runs then returns Success endOfPaginationReached without api call`() =
        runTest {
            // Given
            val dao = FakeFavouriteRestaurantDao()
            val remoteKeyDao = FakeFavouriteRestaurantRemoteKeyDao()
            remoteKeyDao.upsert(
                FavouriteRestaurantRemoteKeyEntity(
                    scope = FAVOURITE_RESTAURANTS_SCOPE,
                    nextPage = null,
                    endOfPaginationReached = true,
                ),
            )
            val api = FakeFavouriteRestaurantsApi()
            val mediator = createMediator(api, dao, remoteKeyDao)

            // When
            val result = mediator.load(LoadType.APPEND, emptyState)

            // Then
            assertIs<RemoteMediator.MediatorResult.Success>(result)
            assertTrue(result.endOfPaginationReached)
            assertEquals(0, api.findFavouriteRestaurantsCallCount)
        }

    @Test
    fun `given page times limit reaches total when loaded then pagination ends and remote key reflects it`() = runTest {
        // Given
        val api = FakeFavouriteRestaurantsApi()
        api.fakeFavouriteRestaurants =
            api.fakeFavouriteRestaurants.copy(items = listOf(Random.nextRestaurantResponse(id = "r1")), total = 1)
        val dao = FakeFavouriteRestaurantDao()
        val remoteKeyDao = FakeFavouriteRestaurantRemoteKeyDao()
        val mediator = createMediator(api, dao, remoteKeyDao)

        // When
        val result = mediator.load(LoadType.REFRESH, emptyState)

        // Then
        assertIs<RemoteMediator.MediatorResult.Success>(result)
        assertTrue(result.endOfPaginationReached)
        val remoteKey = remoteKeyDao.getByScope(FAVOURITE_RESTAURANTS_SCOPE)
        assertEquals(true, remoteKey?.endOfPaginationReached)
        assertNull(remoteKey?.nextPage)
    }

    @Test
    fun `given api throws when load is triggered then returns Error without clearing the existing cache`() = runTest {
        // Given
        val dao = FakeFavouriteRestaurantDao()
        val remoteKeyDao = FakeFavouriteRestaurantRemoteKeyDao()
        dao.upsertAll(listOf(entity(id = "existing-id")))
        val api = FakeFavouriteRestaurantsApi(shouldThrow = true)
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
        val api = FakeFavouriteRestaurantsApi()
        val dao = FakeFavouriteRestaurantDao()
        val remoteKeyDao = FakeFavouriteRestaurantRemoteKeyDao()
        val mediator = createMediator(api, dao, remoteKeyDao)

        // When
        val result = mediator.load(LoadType.PREPEND, emptyState)

        // Then
        assertIs<RemoteMediator.MediatorResult.Success>(result)
        assertTrue(result.endOfPaginationReached)
        assertEquals(0, api.findFavouriteRestaurantsCallCount)
    }

    @Test
    fun `given cache populated after REFRESH when read via TestPager then PagingSource surfaces cached rows`() =
        runTest {
            // Given
            val api = FakeFavouriteRestaurantsApi()
            api.fakeFavouriteRestaurants = api.fakeFavouriteRestaurants.copy(
                items = listOf(Random.nextRestaurantResponse(id = "r1"), Random.nextRestaurantResponse(id = "r2")),
                total = 2,
            )
            val dao = FakeFavouriteRestaurantDao()
            val remoteKeyDao = FakeFavouriteRestaurantRemoteKeyDao()
            val mediator = createMediator(api, dao, remoteKeyDao)

            // When
            mediator.load(LoadType.REFRESH, emptyState)
            val testPager = TestPager(config, dao.pagingSource())
            val loadResult = testPager.refresh()

            // Then
            assertIs<PagingSource.LoadResult.Page<Int, FavouriteRestaurantEntity>>(loadResult)
            assertEquals(listOf("r1", "r2"), loadResult.data.map { it.restaurantId })
        }
}
