package pt.socialfood.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.paging.testing.TestPager
import kotlinx.coroutines.test.runTest
import pt.socialfood.data.local.entity.FavouriteGuideEntity
import pt.socialfood.data.local.entity.FavouriteGuideRemoteKeyEntity
import pt.socialfood.data.local.entity.FavouriteSyncState
import pt.socialfood.data.network.model.author.AuthorResponse
import pt.socialfood.data.network.model.guide.GuideResponse
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.fakes.FakeFavouriteDao
import pt.socialfood.fakes.FakeFavouriteGuideRemoteKeyDao
import pt.socialfood.fakes.FakeFavouritesGuidesApi
import pt.socialfood.mapper.toFavouriteGuideEntity
import pt.socialfood.mapper.toGuide
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalPagingApi::class)
class FavouriteGuideRemoteMediatorTest {
    private val config = PagingConfig(pageSize = 10)
    private val emptyState =
        PagingState<Int, FavouriteGuideEntity>(
            pages = emptyList(),
            anchorPosition = null,
            config = config,
            leadingPlaceholderCount = 0,
        )

    private fun guideResponse(id: String) = GuideResponse(
        id = id,
        name = "Guide $id",
        description = "Description $id",
        visibility = GuideVisibility.PUBLIC,
        author = AuthorResponse(id = "author-id", name = "Author", username = "author", imageUrl = null),
        numberOfRestaurants = 0,
        imageUrl = null,
    )

    private fun entity(id: String, position: Int = 0) = guideResponse(id).toGuide().toFavouriteGuideEntity(
        favouritedAt = 0L,
        syncState = FavouriteSyncState.SYNCED,
        position = position,
    )

    private fun createMediator(
        api: FakeFavouritesGuidesApi,
        dao: FakeFavouriteDao,
        remoteKeyDao: FakeFavouriteGuideRemoteKeyDao,
    ) = FavouriteGuideRemoteMediator(
        favouritesApi = api,
        favouriteDao = dao,
        remoteKeyDao = remoteKeyDao,
        transactionRunner = FavouriteGuideCacheTransactionRunner { it() },
    )

    @Test
    fun `given empty cache when REFRESH runs then fetches page 1 and upserts into dao`() = runTest {
        // Given
        val api = FakeFavouritesGuidesApi()
        api.fakeFavouriteGuides = api.fakeFavouriteGuides.copy(
            items = listOf(guideResponse("g1"), guideResponse("g2")),
            total = 2,
        )
        val dao = FakeFavouriteDao()
        val remoteKeyDao = FakeFavouriteGuideRemoteKeyDao()
        val mediator = createMediator(api, dao, remoteKeyDao)

        // When
        val result = mediator.load(LoadType.REFRESH, emptyState)

        // Then
        assertIs<RemoteMediator.MediatorResult.Success>(result)
        assertEquals(1, api.findFavouriteGuidesCallCount)
        assertEquals(1, api.lastFindFavouriteGuidesPage)
        val cached = dao.getAll()
        assertEquals(listOf("g1", "g2"), cached.map { it.guideId })
    }

    @Test
    fun `given REFRESH succeeds when load completes then old rows and remote key are replaced not merged`() = runTest {
        // Given
        val dao = FakeFavouriteDao()
        val remoteKeyDao = FakeFavouriteGuideRemoteKeyDao()
        dao.upsertAll(listOf(entity(id = "old-id")))
        remoteKeyDao.upsert(
            FavouriteGuideRemoteKeyEntity(scope = FAVOURITE_GUIDES_SCOPE, nextPage = 2, endOfPaginationReached = false),
        )
        val api = FakeFavouritesGuidesApi()
        api.fakeFavouriteGuides = api.fakeFavouriteGuides.copy(items = listOf(guideResponse("new-id")), total = 1)
        val mediator = createMediator(api, dao, remoteKeyDao)

        // When
        val result = mediator.load(LoadType.REFRESH, emptyState)

        // Then
        assertIs<RemoteMediator.MediatorResult.Success>(result)
        val cached = dao.getAll()
        assertEquals(listOf("new-id"), cached.map { it.guideId })
        val remoteKey = remoteKeyDao.getByScope(FAVOURITE_GUIDES_SCOPE)
        assertNull(remoteKey?.nextPage)
        assertEquals(true, remoteKey?.endOfPaginationReached)
    }

    @Test
    fun `given cache has a next page key when APPEND load is triggered then fetches that page and appends it`() =
        runTest {
            // Given
            val dao = FakeFavouriteDao()
            val remoteKeyDao = FakeFavouriteGuideRemoteKeyDao()
            dao.upsertAll(listOf(entity(id = "g1", position = 0)))
            remoteKeyDao.upsert(
                FavouriteGuideRemoteKeyEntity(
                    scope = FAVOURITE_GUIDES_SCOPE,
                    nextPage = 2,
                    endOfPaginationReached = false,
                ),
            )
            val api = FakeFavouritesGuidesApi()
            api.fakeFavouriteGuides = api.fakeFavouriteGuides.copy(items = listOf(guideResponse("g2")), total = 11)
            val mediator = createMediator(api, dao, remoteKeyDao)

            // When
            val result = mediator.load(LoadType.APPEND, emptyState)

            // Then
            assertIs<RemoteMediator.MediatorResult.Success>(result)
            assertEquals(2, api.lastFindFavouriteGuidesPage)
            val cached = dao.getAll()
            assertEquals(listOf("g1", "g2"), cached.map { it.guideId })
        }

    @Test
    fun `given no next page key when APPEND runs then returns Success endOfPaginationReached without api call`() =
        runTest {
            // Given
            val dao = FakeFavouriteDao()
            val remoteKeyDao = FakeFavouriteGuideRemoteKeyDao()
            remoteKeyDao.upsert(
                FavouriteGuideRemoteKeyEntity(
                    scope = FAVOURITE_GUIDES_SCOPE,
                    nextPage = null,
                    endOfPaginationReached = true,
                ),
            )
            val api = FakeFavouritesGuidesApi()
            val mediator = createMediator(api, dao, remoteKeyDao)

            // When
            val result = mediator.load(LoadType.APPEND, emptyState)

            // Then
            assertIs<RemoteMediator.MediatorResult.Success>(result)
            assertTrue(result.endOfPaginationReached)
            assertEquals(0, api.findFavouriteGuidesCallCount)
        }

    @Test
    fun `given page times limit reaches total when loaded then pagination ends and remote key reflects it`() = runTest {
        // Given
        val api = FakeFavouritesGuidesApi()
        api.fakeFavouriteGuides = api.fakeFavouriteGuides.copy(items = listOf(guideResponse("g1")), total = 1)
        val dao = FakeFavouriteDao()
        val remoteKeyDao = FakeFavouriteGuideRemoteKeyDao()
        val mediator = createMediator(api, dao, remoteKeyDao)

        // When
        val result = mediator.load(LoadType.REFRESH, emptyState)

        // Then
        assertIs<RemoteMediator.MediatorResult.Success>(result)
        assertTrue(result.endOfPaginationReached)
        val remoteKey = remoteKeyDao.getByScope(FAVOURITE_GUIDES_SCOPE)
        assertEquals(true, remoteKey?.endOfPaginationReached)
        assertNull(remoteKey?.nextPage)
    }

    @Test
    fun `given api throws when load is triggered then returns Error without clearing the existing cache`() = runTest {
        // Given
        val dao = FakeFavouriteDao()
        val remoteKeyDao = FakeFavouriteGuideRemoteKeyDao()
        dao.upsertAll(listOf(entity(id = "existing-id")))
        val api = FakeFavouritesGuidesApi(shouldThrow = true)
        val mediator = createMediator(api, dao, remoteKeyDao)

        // When
        val result = mediator.load(LoadType.REFRESH, emptyState)

        // Then
        assertIs<RemoteMediator.MediatorResult.Error>(result)
        assertEquals(listOf("existing-id"), dao.getAll().map { it.guideId })
    }

    @Test
    fun `given PREPEND load then returns Success endOfPaginationReached true without calling api`() = runTest {
        // Given
        val api = FakeFavouritesGuidesApi()
        val dao = FakeFavouriteDao()
        val remoteKeyDao = FakeFavouriteGuideRemoteKeyDao()
        val mediator = createMediator(api, dao, remoteKeyDao)

        // When
        val result = mediator.load(LoadType.PREPEND, emptyState)

        // Then
        assertIs<RemoteMediator.MediatorResult.Success>(result)
        assertTrue(result.endOfPaginationReached)
        assertEquals(0, api.findFavouriteGuidesCallCount)
    }

    @Test
    fun `given cache populated after REFRESH when read via TestPager then PagingSource surfaces cached rows`() =
        runTest {
            // Given
            val api = FakeFavouritesGuidesApi()
            api.fakeFavouriteGuides = api.fakeFavouriteGuides.copy(
                items = listOf(guideResponse("g1"), guideResponse("g2")),
                total = 2,
            )
            val dao = FakeFavouriteDao()
            val remoteKeyDao = FakeFavouriteGuideRemoteKeyDao()
            val mediator = createMediator(api, dao, remoteKeyDao)

            // When
            mediator.load(LoadType.REFRESH, emptyState)
            val testPager = TestPager(config, dao.pagingSource())
            val loadResult = testPager.refresh()

            // Then
            assertIs<PagingSource.LoadResult.Page<Int, FavouriteGuideEntity>>(loadResult)
            assertEquals(listOf("g1", "g2"), loadResult.data.map { it.guideId })
        }
}
