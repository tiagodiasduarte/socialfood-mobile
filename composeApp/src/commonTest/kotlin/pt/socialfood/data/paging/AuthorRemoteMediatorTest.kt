package pt.socialfood.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.paging.testing.TestPager
import kotlinx.coroutines.test.runTest
import pt.socialfood.data.local.entity.AuthorEntity
import pt.socialfood.data.local.entity.AuthorRemoteKeyEntity
import pt.socialfood.data.network.model.author.AuthorResponse
import pt.socialfood.fakes.FakeAuthorDao
import pt.socialfood.fakes.FakeAuthorRemoteKeyDao
import pt.socialfood.fakes.FakeAuthorsApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalPagingApi::class)
class AuthorRemoteMediatorTest {
    private val config = PagingConfig(pageSize = 10)
    private val emptyState =
        PagingState<Int, AuthorEntity>(
            pages = emptyList(),
            anchorPosition = null,
            config = config,
            leadingPlaceholderCount = 0,
        )

    private fun authorResponse(id: String) = AuthorResponse(
        id = id,
        name = "Author $id",
        username = "author$id",
        imageUrl = null,
    )

    private fun authorEntity(id: String, position: Int = 0) = AuthorEntity(
        id = id,
        name = "Author $id",
        username = "author$id",
        imageUrl = null,
        position = position,
    )

    private fun createMediator(
        api: FakeAuthorsApi,
        authorDao: FakeAuthorDao,
        authorRemoteKeyDao: FakeAuthorRemoteKeyDao,
    ) = AuthorRemoteMediator(
        authorsApi = api,
        authorDao = authorDao,
        authorRemoteKeyDao = authorRemoteKeyDao,
        transactionRunner = AuthorCacheTransactionRunner { it() },
    )

    @Test
    fun `given empty cache when REFRESH load is triggered then fetches page 1 and upserts into AuthorDao`() = runTest {
        // Given
        val api = FakeAuthorsApi(items = listOf(authorResponse("a1"), authorResponse("a2")), total = 2)
        val authorDao = FakeAuthorDao()
        val authorRemoteKeyDao = FakeAuthorRemoteKeyDao()
        val mediator = createMediator(api, authorDao, authorRemoteKeyDao)

        // When
        val result = mediator.load(LoadType.REFRESH, emptyState)

        // Then
        assertIs<RemoteMediator.MediatorResult.Success>(result)
        assertEquals(1, api.findAuthorsCallCount)
        assertEquals(1, api.lastFindAuthorsPage)
        val cached = authorDao.getAll()
        assertEquals(listOf("a1", "a2"), cached.map { it.id })
    }

    @Test
    fun `given REFRESH succeeds when load completes then old cached rows and the remote key are replaced not merged`() =
        runTest {
            // Given
            val authorDao = FakeAuthorDao()
            val authorRemoteKeyDao = FakeAuthorRemoteKeyDao()
            authorDao.upsertAll(listOf(authorEntity(id = "old-id")))
            authorRemoteKeyDao.upsert(AuthorRemoteKeyEntity(nextPage = 2, endOfPaginationReached = false))
            val api = FakeAuthorsApi(items = listOf(authorResponse("new-id")), total = 1)
            val mediator = createMediator(api, authorDao, authorRemoteKeyDao)

            // When
            val result = mediator.load(LoadType.REFRESH, emptyState)

            // Then
            assertIs<RemoteMediator.MediatorResult.Success>(result)
            val cached = authorDao.getAll()
            assertEquals(listOf("new-id"), cached.map { it.id })
            val remoteKey = authorRemoteKeyDao.get()
            assertNull(remoteKey?.nextPage)
            assertEquals(true, remoteKey?.endOfPaginationReached)
        }

    @Test
    fun `given cache has a next page key when APPEND load is triggered then fetches that page and appends it`() =
        runTest {
            // Given
            val authorDao = FakeAuthorDao()
            val authorRemoteKeyDao = FakeAuthorRemoteKeyDao()
            authorDao.upsertAll(listOf(authorEntity(id = "a1", position = 0)))
            authorRemoteKeyDao.upsert(AuthorRemoteKeyEntity(nextPage = 2, endOfPaginationReached = false))
            val api = FakeAuthorsApi(items = listOf(authorResponse("a2")), total = 11)
            val mediator = createMediator(api, authorDao, authorRemoteKeyDao)

            // When
            val result = mediator.load(LoadType.APPEND, emptyState)

            // Then
            assertIs<RemoteMediator.MediatorResult.Success>(result)
            assertEquals(2, api.lastFindAuthorsPage)
            val cached = authorDao.getAll()
            assertEquals(listOf("a1", "a2"), cached.map { it.id })
        }

    @Test
    @Suppress("MaxLineLength")
    fun `given cache has no next page key when APPEND load is triggered then returns Success with endOfPaginationReached true without calling the api`() =
        runTest {
            // Given
            val authorDao = FakeAuthorDao()
            val authorRemoteKeyDao = FakeAuthorRemoteKeyDao()
            authorRemoteKeyDao.upsert(AuthorRemoteKeyEntity(nextPage = null, endOfPaginationReached = true))
            val api = FakeAuthorsApi()
            val mediator = createMediator(api, authorDao, authorRemoteKeyDao)

            // When
            val result = mediator.load(LoadType.APPEND, emptyState)

            // Then
            assertIs<RemoteMediator.MediatorResult.Success>(result)
            assertTrue(result.endOfPaginationReached)
            assertEquals(0, api.findAuthorsCallCount)
        }

    @Test
    @Suppress("MaxLineLength")
    fun `given response page times limit is greater than or equal to total when load is triggered then endOfPaginationReached is true and the remote key reflects it`() =
        runTest {
            // Given
            val api = FakeAuthorsApi(items = listOf(authorResponse("a1")), total = 1)
            val authorDao = FakeAuthorDao()
            val authorRemoteKeyDao = FakeAuthorRemoteKeyDao()
            val mediator = createMediator(api, authorDao, authorRemoteKeyDao)

            // When
            val result = mediator.load(LoadType.REFRESH, emptyState)

            // Then
            assertIs<RemoteMediator.MediatorResult.Success>(result)
            assertTrue(result.endOfPaginationReached)
            val remoteKey = authorRemoteKeyDao.get()
            assertEquals(true, remoteKey?.endOfPaginationReached)
            assertNull(remoteKey?.nextPage)
        }

    @Test
    @Suppress("MaxLineLength")
    fun `given api throws when load is triggered then returns MediatorResult Error without clearing the existing cache`() =
        runTest {
            // Given
            val authorDao = FakeAuthorDao()
            val authorRemoteKeyDao = FakeAuthorRemoteKeyDao()
            authorDao.upsertAll(listOf(authorEntity(id = "existing-id")))
            val api = FakeAuthorsApi(shouldThrow = true)
            val mediator = createMediator(api, authorDao, authorRemoteKeyDao)

            // When
            val result = mediator.load(LoadType.REFRESH, emptyState)

            // Then
            assertIs<RemoteMediator.MediatorResult.Error>(result)
            assertEquals(listOf("existing-id"), authorDao.getAll().map { it.id })
        }

    @Test
    @Suppress("MaxLineLength")
    fun `given PREPEND load is triggered then returns Success with endOfPaginationReached true immediately without calling the api`() =
        runTest {
            // Given
            val api = FakeAuthorsApi()
            val authorDao = FakeAuthorDao()
            val authorRemoteKeyDao = FakeAuthorRemoteKeyDao()
            val mediator = createMediator(api, authorDao, authorRemoteKeyDao)

            // When
            val result = mediator.load(LoadType.PREPEND, emptyState)

            // Then
            assertIs<RemoteMediator.MediatorResult.Success>(result)
            assertTrue(result.endOfPaginationReached)
            assertEquals(0, api.findAuthorsCallCount)
        }

    @Test
    @Suppress("MaxLineLength")
    fun `given cache is populated after a REFRESH load when read through a TestPager then the PagingSource surfaces the cached rows`() =
        runTest {
            // Given
            val api = FakeAuthorsApi(items = listOf(authorResponse("a1"), authorResponse("a2")), total = 2)
            val authorDao = FakeAuthorDao()
            val authorRemoteKeyDao = FakeAuthorRemoteKeyDao()
            val mediator = createMediator(api, authorDao, authorRemoteKeyDao)

            // When
            mediator.load(LoadType.REFRESH, emptyState)
            val testPager = TestPager(config, authorDao.pagingSource())
            val loadResult = testPager.refresh()

            // Then
            assertIs<PagingSource.LoadResult.Page<Int, AuthorEntity>>(loadResult)
            assertEquals(listOf("a1", "a2"), loadResult.data.map { it.id })
        }
}
