package pt.socialfood.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.paging.testing.TestPager
import kotlinx.coroutines.test.runTest
import pt.socialfood.data.local.entity.GuideEntity
import pt.socialfood.data.local.entity.GuideRemoteKeyEntity
import pt.socialfood.data.network.model.author.AuthorResponse
import pt.socialfood.data.network.model.guide.GuideResponse
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.fakes.FakeGuideDao
import pt.socialfood.fakes.FakeGuideRemoteKeyDao
import pt.socialfood.fakes.FakeGuidesApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

private const val SCOPE = "user-1"

@OptIn(ExperimentalPagingApi::class)
class GuideRemoteMediatorTest {
    private val config = PagingConfig(pageSize = 10)
    private val emptyState =
        PagingState<Int, GuideEntity>(
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

    private fun guideEntity(id: String, scope: String = SCOPE, position: Int = 0) = GuideEntity(
        id = id,
        scope = scope,
        name = "Guide $id",
        description = "Description $id",
        visibility = "PUBLIC",
        authorId = "author-id",
        authorName = "Author",
        authorUsername = "author",
        authorImageUrl = null,
        numberOfRestaurant = 0,
        imageUrl = null,
        position = position,
    )

    private fun createMediator(
        api: FakeGuidesApi,
        guideDao: FakeGuideDao,
        guideRemoteKeyDao: FakeGuideRemoteKeyDao,
        scope: String = SCOPE,
    ) = GuideRemoteMediator(
        scope = scope,
        guidesApi = api,
        guideDao = guideDao,
        guideRemoteKeyDao = guideRemoteKeyDao,
        transactionRunner = GuideCacheTransactionRunner { it() },
    )

    @Test
    fun `given empty cache when REFRESH runs then fetches page 1 and upserts into GuideDao for the scope`() = runTest {
        // Given
        val api = FakeGuidesApi(items = listOf(guideResponse("g1"), guideResponse("g2")), total = 2)
        val guideDao = FakeGuideDao()
        val guideRemoteKeyDao = FakeGuideRemoteKeyDao()
        val mediator = createMediator(api, guideDao, guideRemoteKeyDao)

        // When
        val result = mediator.load(LoadType.REFRESH, emptyState)

        // Then
        assertIs<RemoteMediator.MediatorResult.Success>(result)
        assertEquals(1, api.findGuidesCallCount)
        assertEquals(1, api.lastFindGuidesPage)
        assertEquals(SCOPE, api.lastFindGuidesUserId)
        val cached = guideDao.getAll()
        assertEquals(listOf("g1", "g2"), cached.map { it.id })
        assertTrue(cached.all { it.scope == SCOPE })
    }

    @Test
    fun `given REFRESH succeeds when load completes then old rows and remote key for scope are replaced not merged`() =
        runTest {
            // Given
            val guideDao = FakeGuideDao()
            val guideRemoteKeyDao = FakeGuideRemoteKeyDao()
            guideDao.upsertAll(listOf(guideEntity(id = "old-id")))
            guideRemoteKeyDao.upsert(GuideRemoteKeyEntity(scope = SCOPE, nextPage = 2, endOfPaginationReached = false))
            val api = FakeGuidesApi(items = listOf(guideResponse("new-id")), total = 1)
            val mediator = createMediator(api, guideDao, guideRemoteKeyDao)

            // When
            val result = mediator.load(LoadType.REFRESH, emptyState)

            // Then
            assertIs<RemoteMediator.MediatorResult.Success>(result)
            val cached = guideDao.getAll()
            assertEquals(listOf("new-id"), cached.map { it.id })
            val remoteKey = guideRemoteKeyDao.getByScope(SCOPE)
            assertNull(remoteKey?.nextPage)
            assertEquals(true, remoteKey?.endOfPaginationReached)
        }

    @Test
    fun `given cache has a next page key when APPEND load is triggered then fetches that page and appends it`() =
        runTest {
            // Given
            val guideDao = FakeGuideDao()
            val guideRemoteKeyDao = FakeGuideRemoteKeyDao()
            guideDao.upsertAll(listOf(guideEntity(id = "g1", position = 0)))
            guideRemoteKeyDao.upsert(GuideRemoteKeyEntity(scope = SCOPE, nextPage = 2, endOfPaginationReached = false))
            val api = FakeGuidesApi(items = listOf(guideResponse("g2")), total = 11)
            val mediator = createMediator(api, guideDao, guideRemoteKeyDao)

            // When
            val result = mediator.load(LoadType.APPEND, emptyState)

            // Then
            assertIs<RemoteMediator.MediatorResult.Success>(result)
            assertEquals(2, api.lastFindGuidesPage)
            val cached = guideDao.getAll()
            assertEquals(listOf("g1", "g2"), cached.map { it.id })
        }

    @Test
    fun `given no next page key when APPEND runs then returns Success endOfPaginationReached without api call`() =
        runTest {
            // Given
            val guideDao = FakeGuideDao()
            val guideRemoteKeyDao = FakeGuideRemoteKeyDao()
            guideRemoteKeyDao.upsert(
                GuideRemoteKeyEntity(scope = SCOPE, nextPage = null, endOfPaginationReached = true),
            )
            val api = FakeGuidesApi()
            val mediator = createMediator(api, guideDao, guideRemoteKeyDao)

            // When
            val result = mediator.load(LoadType.APPEND, emptyState)

            // Then
            assertIs<RemoteMediator.MediatorResult.Success>(result)
            assertTrue(result.endOfPaginationReached)
            assertEquals(0, api.findGuidesCallCount)
        }

    @Test
    fun `given page times limit reaches total when loaded then pagination ends and remote key reflects it`() = runTest {
        // Given
        val api = FakeGuidesApi(items = listOf(guideResponse("g1")), total = 1)
        val guideDao = FakeGuideDao()
        val guideRemoteKeyDao = FakeGuideRemoteKeyDao()
        val mediator = createMediator(api, guideDao, guideRemoteKeyDao)

        // When
        val result = mediator.load(LoadType.REFRESH, emptyState)

        // Then
        assertIs<RemoteMediator.MediatorResult.Success>(result)
        assertTrue(result.endOfPaginationReached)
        val remoteKey = guideRemoteKeyDao.getByScope(SCOPE)
        assertEquals(true, remoteKey?.endOfPaginationReached)
        assertNull(remoteKey?.nextPage)
    }

    @Test
    fun `given api throws when load is triggered then returns Error without clearing the existing cache`() = runTest {
        // Given
        val guideDao = FakeGuideDao()
        val guideRemoteKeyDao = FakeGuideRemoteKeyDao()
        guideDao.upsertAll(listOf(guideEntity(id = "existing-id")))
        val api = FakeGuidesApi(shouldThrow = true)
        val mediator = createMediator(api, guideDao, guideRemoteKeyDao)

        // When
        val result = mediator.load(LoadType.REFRESH, emptyState)

        // Then
        assertIs<RemoteMediator.MediatorResult.Error>(result)
        assertEquals(listOf("existing-id"), guideDao.getAll().map { it.id })
    }

    @Test
    fun `given PREPEND load then returns Success endOfPaginationReached true without calling api`() = runTest {
        // Given
        val api = FakeGuidesApi()
        val guideDao = FakeGuideDao()
        val guideRemoteKeyDao = FakeGuideRemoteKeyDao()
        val mediator = createMediator(api, guideDao, guideRemoteKeyDao)

        // When
        val result = mediator.load(LoadType.PREPEND, emptyState)

        // Then
        assertIs<RemoteMediator.MediatorResult.Success>(result)
        assertTrue(result.endOfPaginationReached)
        assertEquals(0, api.findGuidesCallCount)
    }

    @Test
    fun `given cache populated after REFRESH when read via TestPager then PagingSource surfaces cached rows`() =
        runTest {
            // Given
            val api = FakeGuidesApi(items = listOf(guideResponse("g1"), guideResponse("g2")), total = 2)
            val guideDao = FakeGuideDao()
            val guideRemoteKeyDao = FakeGuideRemoteKeyDao()
            val mediator = createMediator(api, guideDao, guideRemoteKeyDao)

            // When
            mediator.load(LoadType.REFRESH, emptyState)
            val testPager = TestPager(config, guideDao.pagingSource(SCOPE))
            val loadResult = testPager.refresh()

            // Then
            assertIs<PagingSource.LoadResult.Page<Int, GuideEntity>>(loadResult)
            assertEquals(listOf("g1", "g2"), loadResult.data.map { it.id })
        }
}
