package pt.socialfood.domain.usecase

import kotlinx.coroutines.test.runTest
import pt.socialfood.domain.model.RecentSearchType
import pt.socialfood.fakes.FakeSettingsRepository
import pt.socialfood.random.nextRecentSearch
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class SaveRecentSearchUseCaseImplTest {
    @Test
    fun `given no recent searches when a search is saved then it becomes the only recent search`() = runTest {
        // Given
        val settingsRepository = FakeSettingsRepository()
        val useCase = SaveRecentSearchUseCaseImpl(settingsRepository)
        val search = Random.nextRecentSearch()

        // When
        val result = useCase(search)

        // Then
        assertEquals(listOf(search), result)
        assertEquals(listOf(search), settingsRepository.getRecentSearches())
    }

    @Test
    fun `given an already recent search when it is saved again then it moves to the front without duplicating`() =
        runTest {
            // Given
            val settingsRepository = FakeSettingsRepository()
            val useCase = SaveRecentSearchUseCaseImpl(settingsRepository)
            val search = Random.nextRecentSearch(id = "s-1", type = RecentSearchType.RESTAURANT)
            val otherSearch = Random.nextRecentSearch(id = "s-2", type = RecentSearchType.GUIDE)
            settingsRepository.saveRecentSearches(listOf(search, otherSearch))

            // When
            val result = useCase(search)

            // Then
            assertEquals(listOf(search, otherSearch), result)
        }

    @Test
    fun `given the same id but a different type when it is saved then both entries are kept`() = runTest {
        // Given
        val settingsRepository = FakeSettingsRepository()
        val useCase = SaveRecentSearchUseCaseImpl(settingsRepository)
        val restaurantSearch = Random.nextRecentSearch(id = "shared-id", type = RecentSearchType.RESTAURANT)
        settingsRepository.saveRecentSearches(listOf(restaurantSearch))
        val guideSearch = Random.nextRecentSearch(id = "shared-id", type = RecentSearchType.GUIDE)

        // When
        val result = useCase(guideSearch)

        // Then
        assertEquals(listOf(guideSearch, restaurantSearch), result)
    }

    @Test
    fun `given 10 recent searches when a new one is saved then the oldest one is dropped`() = runTest {
        // Given
        val settingsRepository = FakeSettingsRepository()
        val useCase = SaveRecentSearchUseCaseImpl(settingsRepository)
        val existingSearches = List(MAX_RECENT_SEARCHES) { index ->
            Random.nextRecentSearch(id = "search-$index")
        }
        settingsRepository.saveRecentSearches(existingSearches)
        val newSearch = Random.nextRecentSearch(id = "search-new")

        // When
        val result = useCase(newSearch)

        // Then
        assertEquals(MAX_RECENT_SEARCHES, result.size)
        assertEquals(newSearch, result.first())
        assertEquals(existingSearches.dropLast(1), result.drop(1))
    }

    private companion object {
        const val MAX_RECENT_SEARCHES = 10
    }
}
