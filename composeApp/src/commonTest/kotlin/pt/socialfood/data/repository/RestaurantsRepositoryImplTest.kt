package pt.socialfood.data.repository

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.ErrorEntity
import pt.socialfood.domain.model.PagedRestaurants
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.fakes.FakeRestaurantApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class RestaurantsRepositoryImplTest {

    private fun createRepository(shouldThrow: Boolean = false): RestaurantsRepositoryImpl =
        RestaurantsRepositoryImpl(FakeRestaurantApi(shouldThrow))

    // importRestaurants

    @Test
    fun `given api returns true when importRestaurants is called then returns Success true`() = runTest {
        // Given
        val repo = createRepository()

        // When
        val result = repo.importRestaurants()

        // Then
        assertIs<Result.Success<Boolean>>(result)
        assertEquals(true, result.data)
    }

    @Test
    fun `given api throws when importRestaurants is called then returns Error Unknown`() = runTest {
        // Given
        val repo = createRepository(shouldThrow = true)

        // When
        val result = repo.importRestaurants()

        // Then
        assertIs<Result.Error>(result)
        assertEquals(ErrorEntity.Unknown, result.error)
    }

    // delete

    @Test
    fun `given valid id when delete is called then returns Success true`() = runTest {
        // Given
        val repo = createRepository()

        // When
        val result = repo.delete(id = "restaurant-id")

        // Then
        assertIs<Result.Success<Boolean>>(result)
        assertEquals(true, result.data)
    }

    @Test
    fun `given api throws when delete is called then returns Error Unknown`() = runTest {
        // Given
        val repo = createRepository(shouldThrow = true)

        // When
        val result = repo.delete(id = "restaurant-id")

        // Then
        assertIs<Result.Error>(result)
        assertEquals(ErrorEntity.Unknown, result.error)
    }

    // findAll

    @Test
    fun `given restaurants exist when findAll is called then returns Success with list`() = runTest {
        // Given
        val repo = createRepository()

        // When
        val result = repo.findAll()

        // Then
        assertIs<Result.Success<List<Restaurant>>>(result)
        assertTrue(result.data.isNotEmpty())
        assertEquals("restaurant-id", result.data.first().id)
    }

    @Test
    fun `given api throws when findAll is called then returns Error Unknown`() = runTest {
        // Given
        val repo = createRepository(shouldThrow = true)

        // When
        val result = repo.findAll()

        // Then
        assertIs<Result.Error>(result)
        assertEquals(ErrorEntity.Unknown, result.error)
    }

    // findRestaurants

    @Test
    fun `given valid pagination params when findRestaurants is called then returns Success with PagedRestaurants and hasMore true`() = runTest {
        // Given
        val repo = createRepository()
        val page = 1
        val limit = 10
        // FakeRestaurantApi returns total = 25, so page * limit = 10 < 25 → hasMore = true

        // When
        val result = repo.findRestaurants(page = page, limit = limit, query = null)

        // Then
        assertIs<Result.Success<PagedRestaurants>>(result)
        assertEquals(page, result.data.page)
        assertEquals(25, result.data.total)
        assertTrue(result.data.hasMore)
    }

    @Test
    fun `given api throws when findRestaurants is called then returns Error Unknown`() = runTest {
        // Given
        val repo = createRepository(shouldThrow = true)

        // When
        val result = repo.findRestaurants(page = 1, limit = 10, query = null)

        // Then
        assertIs<Result.Error>(result)
        assertEquals(ErrorEntity.Unknown, result.error)
    }

    // findById

    @Test
    fun `given valid id when findById is called then returns Success with Restaurant`() = runTest {
        // Given
        val repo = createRepository()

        // When
        val result = repo.findById(id = "restaurant-id")

        // Then
        assertIs<Result.Success<Restaurant>>(result)
        assertEquals("restaurant-id", result.data.id)
    }

    @Test
    fun `given api throws when findById is called then returns Error Unknown`() = runTest {
        // Given
        val repo = createRepository(shouldThrow = true)

        // When
        val result = repo.findById(id = "restaurant-id")

        // Then
        assertIs<Result.Error>(result)
        assertEquals(ErrorEntity.Unknown, result.error)
    }

    // findByPlaceId

    @Test
    fun `given valid placeId when findByPlaceId is called then returns Success with Restaurant`() = runTest {
        // Given
        val repo = createRepository()

        // When
        val result = repo.findByPlaceId(placeId = "place-id")

        // Then
        assertIs<Result.Success<Restaurant>>(result)
        assertEquals("restaurant-id", result.data.id)
    }

    @Test
    fun `given api throws when findByPlaceId is called then returns Error Unknown`() = runTest {
        // Given
        val repo = createRepository(shouldThrow = true)

        // When
        val result = repo.findByPlaceId(placeId = "place-id")

        // Then
        assertIs<Result.Error>(result)
        assertEquals(ErrorEntity.Unknown, result.error)
    }

    // addByPlaceId

    @Test
    fun `given valid placeId when addByPlaceId is called then returns Success`() = runTest {
        // Given
        val repo = createRepository()

        // When
        val result = repo.addByPlaceId(placeId = "place-id")

        // Then
        assertIs<Result.Success<Unit>>(result)
    }

    @Test
    fun `given api throws when addByPlaceId is called then returns Error Unknown`() = runTest {
        // Given
        val repo = createRepository(shouldThrow = true)

        // When
        val result = repo.addByPlaceId(placeId = "place-id")

        // Then
        assertIs<Result.Error>(result)
        assertEquals(ErrorEntity.Unknown, result.error)
    }

    // update

    @Test
    fun `given valid params when update is called then returns Success with updated Restaurant`() = runTest {
        // Given
        val repo = createRepository()

        // When
        val result = repo.update(
            id = "restaurant-id",
            name = "Updated Name",
            description = "Updated Description",
            country = "Portugal",
            city = "Lisbon",
            address = "Rua Example, 1",
            phoneNumber = "+351210000000",
            websiteUrl = "https://example.com",
        )

        // Then
        assertIs<Result.Success<Restaurant>>(result)
        assertEquals("restaurant-id", result.data.id)
    }

    @Test
    fun `given api throws when update is called then returns Error Unknown`() = runTest {
        // Given
        val repo = createRepository(shouldThrow = true)

        // When
        val result = repo.update(
            id = "restaurant-id",
            name = "Updated Name",
            description = "Updated Description",
            country = "Portugal",
            city = "Lisbon",
            address = "Rua Example, 1",
            phoneNumber = "+351210000000",
            websiteUrl = "https://example.com",
        )

        // Then
        assertIs<Result.Error>(result)
        assertEquals(ErrorEntity.Unknown, result.error)
    }
}
