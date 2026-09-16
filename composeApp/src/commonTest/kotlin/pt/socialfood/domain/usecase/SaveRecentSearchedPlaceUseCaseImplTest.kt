package pt.socialfood.domain.usecase

import kotlinx.coroutines.test.runTest
import pt.socialfood.fakes.FakeSettingsRepository
import pt.socialfood.random.nextPlace
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class SaveRecentSearchedPlaceUseCaseImplTest {
    @Test
    fun `given no recent places when a place is saved then it becomes the only recent place`() = runTest {
        // Given
        val settingsRepository = FakeSettingsRepository()
        val useCase = SaveRecentSearchedPlaceUseCaseImpl(settingsRepository)
        val place = Random.nextPlace()

        // When
        val result = useCase(place)

        // Then
        assertEquals(listOf(place), result)
        assertEquals(listOf(place), settingsRepository.getRecentSearchedPlaces())
    }

    @Test
    fun `given an already recent place when it is saved again then it moves to the front without duplicating`() =
        runTest {
            // Given
            val settingsRepository = FakeSettingsRepository()
            val useCase = SaveRecentSearchedPlaceUseCaseImpl(settingsRepository)
            val place = Random.nextPlace(id = "place-1")
            val otherPlace = Random.nextPlace(id = "place-2")
            settingsRepository.saveRecentSearchedPlaces(listOf(place, otherPlace))

            // When
            val result = useCase(place)

            // Then
            assertEquals(listOf(place, otherPlace), result)
        }

    @Test
    fun `given 10 recent places when a new place is saved then the oldest one is dropped`() = runTest {
        // Given
        val settingsRepository = FakeSettingsRepository()
        val useCase = SaveRecentSearchedPlaceUseCaseImpl(settingsRepository)
        val existingPlaces = List(MAX_RECENT_SEARCHED_PLACES) { index -> Random.nextPlace(id = "place-$index") }
        settingsRepository.saveRecentSearchedPlaces(existingPlaces)
        val newPlace = Random.nextPlace(id = "place-new")

        // When
        val result = useCase(newPlace)

        // Then
        assertEquals(MAX_RECENT_SEARCHED_PLACES, result.size)
        assertEquals(newPlace, result.first())
        assertEquals(existingPlaces.dropLast(1), result.drop(1))
    }

    private companion object {
        const val MAX_RECENT_SEARCHED_PLACES = 10
    }
}
