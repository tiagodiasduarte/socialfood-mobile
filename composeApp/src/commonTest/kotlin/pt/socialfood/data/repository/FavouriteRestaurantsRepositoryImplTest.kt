package pt.socialfood.data.repository

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.data.local.entity.FavouriteSyncState
import pt.socialfood.domain.model.Location
import pt.socialfood.domain.model.PagedFavouriteRestaurants
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.fakes.FakeFavouriteRestaurantDao
import pt.socialfood.fakes.FakeFavouriteRestaurantsApi
import pt.socialfood.fakes.FakeSettingsRepository
import pt.socialfood.mapper.toFavouriteRestaurantEntity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class FavouriteRestaurantsRepositoryImplTest {
    private val fakeRestaurant =
        Restaurant(
            id = "restaurant-id",
            name = "Restaurant Name",
            description = "Restaurant Description",
            city = "Lisbon",
            country = "Portugal",
            countryCode = "PT",
            postalCode = "1000-000",
            photoNames = emptyList(),
            address = "Rua Augusta 1",
            rating = 4.5,
            userRatingCount = 100,
            websiteUrl = null,
            phoneNumber = "+351910000000",
            location = Location(latitude = 38.7223, longitude = -9.1393),
        )

    @OptIn(ExperimentalTime::class)
    private fun now(): Long = Clock.System.now().toEpochMilliseconds()

    private fun createRepository(
        api: FakeFavouriteRestaurantsApi = FakeFavouriteRestaurantsApi(),
        dao: FakeFavouriteRestaurantDao = FakeFavouriteRestaurantDao(),
        settings: FakeSettingsRepository = FakeSettingsRepository(),
    ): Triple<FavouriteRestaurantsRepositoryImpl, FakeFavouriteRestaurantDao, FakeSettingsRepository> =
        Triple(FavouriteRestaurantsRepositoryImpl(api, dao, settings), dao, settings)

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

    // getFavouritesPaged

    @Test
    fun `given cached favourites when getFavouritesPaged is called then reads from DAO only and never calls the API`() =
        runTest {
            // Given
            val (repo, dao, _) = createRepository(api = FakeFavouriteRestaurantsApi(shouldThrow = true))
            dao.upsert(fakeRestaurant.toFavouriteRestaurantEntityForTest(FavouriteSyncState.SYNCED))

            // When
            val result = repo.getFavouritesPaged(page = 1, limit = 10)

            // Then
            assertIs<Result.Success<PagedFavouriteRestaurants>>(result)
            assertEquals(1, result.data.favourites.size)
            assertEquals(
                fakeRestaurant.id,
                result.data.favourites
                    .first()
                    .restaurant.id,
            )
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
        assertTrue(dao.getPaged(limit = 10, offset = 0).isNotEmpty())
    }

    @Test
    fun `given DAO write throws when syncFavourites is called then does not advance syncedAt`() = runTest {
        // Given
        val (repo, _, settings) = createRepository(dao = FakeFavouriteRestaurantDao(shouldThrowOnWrite = true))
        settings.saveLastFavouriteRestaurantsSyncAttemptAt(0L)

        // When
        val result = repo.syncFavourites()

        // Then
        assertIs<Result.Failure>(result)
        assertEquals(null, settings.getLastFavouriteRestaurantsSyncedAt())
    }

    @Test
    @Suppress("MaxLineLength", "ktlint:standard:max-line-length")
    fun `given last sync attempt was recent when syncFavourites is called then returns early without calling the API`() =
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
    )
