package pt.socialfood.data.repository

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.ErrorEntity
import pt.socialfood.domain.model.Place
import pt.socialfood.fakes.FakeCrashReporter
import pt.socialfood.fakes.FakePlacesApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class PlacesRepositoryImplTest {

    private val crashReporter = FakeCrashReporter()

    private fun createRepository(shouldThrow: Boolean = false): PlacesRepositoryImpl =
        PlacesRepositoryImpl(FakePlacesApi(shouldThrow), crashReporter)

    @Test
    fun `given a valid query when search is called then returns Success with places`() = runTest {
        // Given
        val repo = createRepository()

        // When
        val result = repo.search("pizza")

        // Then
        assertIs<Result.Success<List<Place>>>(result)
        assertTrue(result.data.isNotEmpty())
        assertEquals("place-id", result.data.first().id)
        assertEquals("Place Name", result.data.first().name)
    }

    @Test
    fun `given api throws when search is called then returns Error Unknown`() = runTest {
        // Given
        val repo = createRepository(shouldThrow = true)

        // When
        val result = repo.search("pizza")

        // Then
        assertIs<Result.Error>(result)
        assertEquals(ErrorEntity.Unknown, result.error)
        assertEquals(1, crashReporter.recordedExceptions.size)
    }
}
