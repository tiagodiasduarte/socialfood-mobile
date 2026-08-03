package pt.socialfood.data.repository

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.data.local.entity.FavouriteSyncState
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.domain.model.PagedFavouriteGuides
import pt.socialfood.fakes.FakeFavouriteDao
import pt.socialfood.fakes.FakeFavouritesApi
import pt.socialfood.fakes.FakeSettingsRepository
import pt.socialfood.mapper.toFavouriteGuideEntity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class FavouritesRepositoryImplTest {
    private val fakeGuide =
        Guide(
            id = "guide-id",
            name = "Guide Name",
            description = "Guide Description",
            visibility = GuideVisibility.PUBLIC,
            author = Author(id = "author-id", name = "Author Name", username = "authorname", imageUrl = null),
            numberOfRestaurant = 0,
            imageUrl = null,
        )

    @OptIn(ExperimentalTime::class)
    private fun now(): Long = Clock.System.now().toEpochMilliseconds()

    private fun createRepository(
        api: FakeFavouritesApi = FakeFavouritesApi(),
        dao: FakeFavouriteDao = FakeFavouriteDao(),
        settings: FakeSettingsRepository = FakeSettingsRepository(),
    ): Triple<FavouritesRepositoryImpl, FakeFavouriteDao, FakeSettingsRepository> =
        Triple(FavouritesRepositoryImpl(api, dao, settings), dao, settings)

    // markFavourite

    @Test
    fun `given api succeeds when markFavourite is called then persists SYNCED entity and returns Success`() =
        runTest {
            // Given
            val (repo, dao, _) = createRepository()

            // When
            val result = repo.markFavourite(fakeGuide)

            // Then
            assertIs<Result.Success<Unit>>(result)
            val stored = dao.getByGuideId(fakeGuide.id)
            assertEquals(FavouriteSyncState.SYNCED.name, stored?.syncState)
        }

    @Test
    fun `given api throws when markFavourite is called then still returns Success with entity left PENDING_ADD`() =
        runTest {
            // Given
            val (repo, dao, _) = createRepository(api = FakeFavouritesApi(shouldThrow = true))

            // When
            val result = repo.markFavourite(fakeGuide)

            // Then
            assertIs<Result.Success<Unit>>(result)
            val stored = dao.getByGuideId(fakeGuide.id)
            assertEquals(FavouriteSyncState.PENDING_ADD.name, stored?.syncState)
        }

    // unmarkFavourite

    @Test
    fun `given api succeeds when unmarkFavourite is called then removes entity and returns Success`() =
        runTest {
            // Given
            val (repo, dao, _) = createRepository()
            dao.upsert(fakeGuide.toFavouriteGuideEntityForTest(FavouriteSyncState.SYNCED))

            // When
            val result = repo.unmarkFavourite(fakeGuide.id)

            // Then
            assertIs<Result.Success<Unit>>(result)
            assertEquals(null, dao.getByGuideId(fakeGuide.id))
        }

    @Test
    fun `given api throws when unmarkFavourite is called then still returns Success with entity left PENDING_REMOVE`() =
        runTest {
            // Given
            val (repo, dao, _) = createRepository(api = FakeFavouritesApi(shouldThrow = true))
            dao.upsert(fakeGuide.toFavouriteGuideEntityForTest(FavouriteSyncState.SYNCED))

            // When
            val result = repo.unmarkFavourite(fakeGuide.id)

            // Then
            assertIs<Result.Success<Unit>>(result)
            val stored = dao.getByGuideId(fakeGuide.id)
            assertEquals(FavouriteSyncState.PENDING_REMOVE.name, stored?.syncState)
        }

    // getFavouritesPaged

    @Test
    fun `given cached favourites when getFavouritesPaged is called then reads from DAO only and never calls the API`() =
        runTest {
            // Given
            val (repo, dao, _) = createRepository(api = FakeFavouritesApi(shouldThrow = true))
            dao.upsert(fakeGuide.toFavouriteGuideEntityForTest(FavouriteSyncState.SYNCED))

            // When
            val result = repo.getFavouritesPaged(page = 1, limit = 10)

            // Then
            assertIs<Result.Success<PagedFavouriteGuides>>(result)
            assertEquals(1, result.data.favourites.size)
            assertEquals(
                fakeGuide.id,
                result.data.favourites
                    .first()
                    .guide.id,
            )
        }

    // syncFavourites

    @Test
    fun `given changes available when syncFavourites is called then applies them and advances syncedAt`() =
        runTest {
            // Given
            val (repo, dao, settings) = createRepository()
            settings.saveLastFavouritesSyncAttemptAt(0L)

            // When
            val result = repo.syncFavourites()

            // Then
            assertIs<Result.Success<Unit>>(result)
            assertEquals("2026-08-01T10:30:00Z", settings.getLastFavouritesSyncedAt())
            assertTrue(dao.getPaged(limit = 10, offset = 0).isNotEmpty())
        }

    @Test
    fun `given DAO write throws when syncFavourites is called then does not advance syncedAt`() =
        runTest {
            // Given
            val (repo, _, settings) = createRepository(dao = FakeFavouriteDao(shouldThrowOnWrite = true))
            settings.saveLastFavouritesSyncAttemptAt(0L)

            // When
            val result = repo.syncFavourites()

            // Then
            assertIs<Result.Failure>(result)
            assertEquals(null, settings.getLastFavouritesSyncedAt())
        }

    @Test
    @Suppress("MaxLineLength")
    fun `given last sync attempt was recent when syncFavourites is called then returns early without calling the API`() =
        runTest {
            // Given
            val (repo, _, settings) = createRepository(api = FakeFavouritesApi(shouldThrow = true))
            settings.saveLastFavouritesSyncAttemptAt(now())

            // When
            val result = repo.syncFavourites()

            // Then
            assertIs<Result.Success<Unit>>(result)
            assertEquals(null, settings.getLastFavouritesSyncedAt())
        }

    @Test
    fun `given last sync attempt was long ago when syncFavourites is called then proceeds`() =
        runTest {
            // Given
            val (repo, _, settings) = createRepository()
            settings.saveLastFavouritesSyncAttemptAt(0L)

            // When
            val result = repo.syncFavourites()

            // Then
            assertIs<Result.Success<Unit>>(result)
            assertEquals("2026-08-01T10:30:00Z", settings.getLastFavouritesSyncedAt())
        }
}

@OptIn(ExperimentalTime::class)
private fun Guide.toFavouriteGuideEntityForTest(syncState: FavouriteSyncState) =
    this.toFavouriteGuideEntity(
        favouritedAt = Clock.System.now().toEpochMilliseconds(),
        syncState = syncState,
    )
