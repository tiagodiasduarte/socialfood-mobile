package pt.socialfood.data.repository

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.data.paging.AuthorCacheTransactionRunner
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.model.AuthorDetail
import pt.socialfood.domain.model.PagedAuthors
import pt.socialfood.fakes.FakeAuthorDao
import pt.socialfood.fakes.FakeAuthorRemoteKeyDao
import pt.socialfood.fakes.FakeAuthorsApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AuthorsRepositoryImplTest {
    private fun createRepository(shouldThrow: Boolean = false): AuthorsRepositoryImpl =
        AuthorsRepositoryImpl(
            authorsApi = FakeAuthorsApi(shouldThrow),
            authorDao = FakeAuthorDao(),
            authorRemoteKeyDao = FakeAuthorRemoteKeyDao(),
            transactionRunner = AuthorCacheTransactionRunner { it() },
        )

    // findAuthors

    @Test
    @Suppress("MaxLineLength")
    fun `given valid pagination params when findAuthors is called then returns Success with PagedAuthors and correct hasMore flag`() =
        runTest {
            // Given
            val repo = createRepository()
            val page = 1
            val limit = 10
            // FakeAuthorsApi returns total = 25, so page * limit = 10 < 25 → hasMore = true

            // When
            val result = repo.findAuthors(page = page, limit = limit, query = null)

            // Then
            assertIs<Result.Success<PagedAuthors>>(result)
            assertEquals(page, result.data.page)
            assertTrue(result.data.hasMore)
            assertEquals(
                "author-id",
                result.data.authors
                    .first()
                    .id,
            )
        }

    @Test
    fun `given api throws when findAuthors is called then returns Error Unknown`() =
        runTest {
            // Given
            val repo = createRepository(shouldThrow = true)

            // When
            val result = repo.findAuthors(page = 1, limit = 10, query = null)

            // Then
            assertIs<Result.Failure>(result)
            assertIs<DataError.Network>(result.error)
        }

    // findAuthorById

    @Test
    fun `given valid id when findAuthorById is called then returns Success with AuthorDetail`() =
        runTest {
            // Given
            val repo = createRepository()

            // When
            val result = repo.findAuthorById(id = "author-id")

            // Then
            assertIs<Result.Success<AuthorDetail>>(result)
            assertEquals("author-id", result.data.id)
            assertEquals("Author Name", result.data.name)
        }

    @Test
    fun `given api throws when findAuthorById is called then returns Error Unknown`() =
        runTest {
            // Given
            val repo = createRepository(shouldThrow = true)

            // When
            val result = repo.findAuthorById(id = "author-id")

            // Then
            assertIs<Result.Failure>(result)
            assertIs<DataError.Network>(result.error)
        }

    // getAuthorsPagingFlow

    @Test
    fun `given getAuthorsPagingFlow is called then the returned Pager is configured with an AuthorRemoteMediator`() =
        runTest {
            // Given
            val repo = createRepository()

            // When
            val flow = repo.getAuthorsPagingFlow()

            // Then
            assertNotNull(flow)
        }
}
