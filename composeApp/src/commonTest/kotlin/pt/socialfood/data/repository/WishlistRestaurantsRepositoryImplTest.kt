package pt.socialfood.data.repository

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.data.local.entity.WishlistSyncState
import pt.socialfood.domain.model.PagedWishlistRestaurants
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.fakes.FakeSettingsRepository
import pt.socialfood.fakes.FakeWishlistRestaurantDao
import pt.socialfood.fakes.FakeWishlistRestaurantsApi
import pt.socialfood.mapper.toWishlistRestaurantEntity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class WishlistRestaurantsRepositoryImplTest {
    private val fakeRestaurant = Restaurant(
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
    )

    @OptIn(ExperimentalTime::class)
    private fun now(): Long = Clock.System.now().toEpochMilliseconds()

    private fun createRepository(
        api: FakeWishlistRestaurantsApi = FakeWishlistRestaurantsApi(),
        dao: FakeWishlistRestaurantDao = FakeWishlistRestaurantDao(),
        settings: FakeSettingsRepository = FakeSettingsRepository(),
    ): Triple<WishlistRestaurantsRepositoryImpl, FakeWishlistRestaurantDao, FakeSettingsRepository> =
        Triple(WishlistRestaurantsRepositoryImpl(api, dao, settings), dao, settings)

    // markWishlisted

    @Test
    fun `given api succeeds when markWishlisted is called then persists SYNCED entity and returns Success`() = runTest {
        // Given
        val (repo, dao, _) = createRepository()

        // When
        val result = repo.markWishlisted(fakeRestaurant)

        // Then
        assertIs<Result.Success<Unit>>(result)
        val stored = dao.getByRestaurantId(fakeRestaurant.id)
        assertEquals(WishlistSyncState.SYNCED.name, stored?.syncState)
    }

    @Test
    fun `given api throws when markWishlisted is called then still returns Success with entity left PENDING_ADD`() =
        runTest {
            // Given
            val (repo, dao, _) = createRepository(api = FakeWishlistRestaurantsApi(shouldThrow = true))

            // When
            val result = repo.markWishlisted(fakeRestaurant)

            // Then
            assertIs<Result.Success<Unit>>(result)
            val stored = dao.getByRestaurantId(fakeRestaurant.id)
            assertEquals(WishlistSyncState.PENDING_ADD.name, stored?.syncState)
        }

    // unmarkWishlisted

    @Test
    fun `given api succeeds when unmarkWishlisted is called then removes entity and returns Success`() = runTest {
        // Given
        val (repo, dao, _) = createRepository()
        dao.upsert(fakeRestaurant.toWishlistRestaurantEntityForTest(WishlistSyncState.SYNCED))

        // When
        val result = repo.unmarkWishlisted(fakeRestaurant.id)

        // Then
        assertIs<Result.Success<Unit>>(result)
        assertEquals(null, dao.getByRestaurantId(fakeRestaurant.id))
    }

    @Test
    @Suppress("MaxLineLength", "ktlint:standard:max-line-length")
    fun `given api throws when unmarkWishlisted is called then still returns Success with entity left PENDING_REMOVE`() =
        runTest {
            // Given
            val (repo, dao, _) = createRepository(api = FakeWishlistRestaurantsApi(shouldThrow = true))
            dao.upsert(fakeRestaurant.toWishlistRestaurantEntityForTest(WishlistSyncState.SYNCED))

            // When
            val result = repo.unmarkWishlisted(fakeRestaurant.id)

            // Then
            assertIs<Result.Success<Unit>>(result)
            val stored = dao.getByRestaurantId(fakeRestaurant.id)
            assertEquals(WishlistSyncState.PENDING_REMOVE.name, stored?.syncState)
        }

    // getWishlistPaged

    @Test
    fun `given cached wishlist when getWishlistPaged is called then reads from DAO only and never calls the API`() =
        runTest {
            // Given
            val (repo, dao, _) = createRepository(api = FakeWishlistRestaurantsApi(shouldThrow = true))
            dao.upsert(fakeRestaurant.toWishlistRestaurantEntityForTest(WishlistSyncState.SYNCED))

            // When
            val result = repo.getWishlistPaged(page = 1, limit = 10)

            // Then
            assertIs<Result.Success<PagedWishlistRestaurants>>(result)
            assertEquals(1, result.data.wishlist.size)
            assertEquals(
                fakeRestaurant.id,
                result.data.wishlist
                    .first()
                    .restaurant.id,
            )
        }

    // syncWishlist

    @Test
    fun `given changes available when syncWishlist is called then applies them and advances syncedAt`() = runTest {
        // Given
        val (repo, dao, settings) = createRepository()
        settings.saveLastWishlistRestaurantsSyncAttemptAt(0L)

        // When
        val result = repo.syncWishlist()

        // Then
        assertIs<Result.Success<Unit>>(result)
        assertEquals("2026-08-01T10:30:00Z", settings.getLastWishlistRestaurantsSyncedAt())
        assertTrue(dao.getPaged(limit = 10, offset = 0).isNotEmpty())
    }

    @Test
    fun `given DAO write throws when syncWishlist is called then does not advance syncedAt`() = runTest {
        // Given
        val (repo, _, settings) = createRepository(dao = FakeWishlistRestaurantDao(shouldThrowOnWrite = true))
        settings.saveLastWishlistRestaurantsSyncAttemptAt(0L)

        // When
        val result = repo.syncWishlist()

        // Then
        assertIs<Result.Failure>(result)
        assertEquals(null, settings.getLastWishlistRestaurantsSyncedAt())
    }

    @Test
    @Suppress("MaxLineLength", "ktlint:standard:max-line-length")
    fun `given last sync attempt was recent when syncWishlist is called then returns early without calling the API`() =
        runTest {
            // Given
            val (repo, _, settings) = createRepository(api = FakeWishlistRestaurantsApi(shouldThrow = true))
            settings.saveLastWishlistRestaurantsSyncAttemptAt(now())

            // When
            val result = repo.syncWishlist()

            // Then
            assertIs<Result.Success<Unit>>(result)
            assertEquals(null, settings.getLastWishlistRestaurantsSyncedAt())
        }

    @Test
    fun `given last sync attempt was long ago when syncWishlist is called then proceeds`() = runTest {
        // Given
        val (repo, _, settings) = createRepository()
        settings.saveLastWishlistRestaurantsSyncAttemptAt(0L)

        // When
        val result = repo.syncWishlist()

        // Then
        assertIs<Result.Success<Unit>>(result)
        assertEquals("2026-08-01T10:30:00Z", settings.getLastWishlistRestaurantsSyncedAt())
    }
}

@OptIn(ExperimentalTime::class)
private fun Restaurant.toWishlistRestaurantEntityForTest(syncState: WishlistSyncState) =
    this.toWishlistRestaurantEntity(
        wishlistedAt = Clock.System.now().toEpochMilliseconds(),
        syncState = syncState,
    )
