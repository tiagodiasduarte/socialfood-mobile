package pt.socialfood.data.repository

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.ErrorEntity
import pt.socialfood.domain.model.HomeItemType
import pt.socialfood.domain.model.HomeSection
import pt.socialfood.domain.model.HomeSectionType
import pt.socialfood.fakes.FakeCrashReporter
import pt.socialfood.fakes.FakeHomeApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class HomeRepositoryImplTest {

    private val crashReporter = FakeCrashReporter()

    private fun createRepository(shouldThrow: Boolean = false): HomeRepositoryImpl =
        HomeRepositoryImpl(FakeHomeApi(shouldThrow), crashReporter)

    // findAll

    @Test
    fun `given sections exist when findAll is called then returns Success with list`() = runTest {
        // Given
        val repo = createRepository()

        // When
        val result = repo.findAll()

        // Then
        assertIs<Result.Success<List<HomeSection>>>(result)
        assertTrue(result.data.isNotEmpty())
        assertEquals("section-id", result.data.first().id)
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
        assertEquals(1, crashReporter.recordedExceptions.size)
    }

    // findById

    @Test
    fun `given valid id when findById is called then returns Success with HomeSection`() = runTest {
        // Given
        val repo = createRepository()

        // When
        val result = repo.findById(id = "section-id")

        // Then
        assertIs<Result.Success<HomeSection>>(result)
        assertEquals("section-id", result.data.id)
    }

    @Test
    fun `given api throws when findById is called then returns Error Unknown`() = runTest {
        // Given
        val repo = createRepository(shouldThrow = true)

        // When
        val result = repo.findById(id = "section-id")

        // Then
        assertIs<Result.Error>(result)
        assertEquals(ErrorEntity.Unknown, result.error)
    }

    // create

    @Test
    fun `given valid params when create is called then returns Success with HomeSection`() = runTest {
        // Given
        val repo = createRepository()

        // When
        val result = repo.create(title = "Section Title", type = HomeSectionType.RESTAURANT_LIST, position = 0)

        // Then
        assertIs<Result.Success<HomeSection>>(result)
        assertEquals("section-id", result.data.id)
        assertEquals("Section Title", result.data.title)
    }

    @Test
    fun `given api throws when create is called then returns Error Unknown`() = runTest {
        // Given
        val repo = createRepository(shouldThrow = true)

        // When
        val result = repo.create(title = "Section Title", type = HomeSectionType.RESTAURANT_LIST, position = 0)

        // Then
        assertIs<Result.Error>(result)
        assertEquals(ErrorEntity.Unknown, result.error)
    }

    // update

    @Test
    fun `given valid params when update is called then returns Success with HomeSection`() = runTest {
        // Given
        val repo = createRepository()

        // When
        val result = repo.update(
            id = "section-id",
            title = "Section Title",
            position = 0,
            isActive = true,
            restaurantIds = emptyList(),
            guideIds = emptyList(),
        )

        // Then
        assertIs<Result.Success<HomeSection>>(result)
        assertEquals("section-id", result.data.id)
    }

    @Test
    fun `given api throws when update is called then returns Error Unknown`() = runTest {
        // Given
        val repo = createRepository(shouldThrow = true)

        // When
        val result = repo.update(
            id = "section-id",
            title = "Section Title",
            position = 0,
            isActive = true,
            restaurantIds = emptyList(),
            guideIds = emptyList(),
        )

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
        val result = repo.delete(id = "section-id")

        // Then
        assertIs<Result.Success<Boolean>>(result)
        assertEquals(true, result.data)
    }

    @Test
    fun `given api throws when delete is called then returns Error Unknown`() = runTest {
        // Given
        val repo = createRepository(shouldThrow = true)

        // When
        val result = repo.delete(id = "section-id")

        // Then
        assertIs<Result.Error>(result)
        assertEquals(ErrorEntity.Unknown, result.error)
    }

    // addItem

    @Test
    fun `given valid params when addItem is called then returns Success with HomeSection`() = runTest {
        // Given
        val repo = createRepository()

        // When
        val result = repo.addItem(
            sectionId = "section-id",
            itemId = "item-id",
            itemType = HomeItemType.RESTAURANT,
            position = 0,
        )

        // Then
        assertIs<Result.Success<HomeSection>>(result)
        assertEquals("section-id", result.data.id)
    }

    @Test
    fun `given api throws when addItem is called then returns Error Unknown`() = runTest {
        // Given
        val repo = createRepository(shouldThrow = true)

        // When
        val result = repo.addItem(
            sectionId = "section-id",
            itemId = "item-id",
            itemType = HomeItemType.RESTAURANT,
            position = 0,
        )

        // Then
        assertIs<Result.Error>(result)
        assertEquals(ErrorEntity.Unknown, result.error)
    }

    // removeItem

    @Test
    fun `given valid ids when removeItem is called then returns Success true`() = runTest {
        // Given
        val repo = createRepository()

        // When
        val result = repo.removeItem(sectionId = "section-id", itemId = "item-id")

        // Then
        assertIs<Result.Success<Boolean>>(result)
        assertEquals(true, result.data)
    }

    @Test
    fun `given api throws when removeItem is called then returns Error Unknown`() = runTest {
        // Given
        val repo = createRepository(shouldThrow = true)

        // When
        val result = repo.removeItem(sectionId = "section-id", itemId = "item-id")

        // Then
        assertIs<Result.Error>(result)
        assertEquals(ErrorEntity.Unknown, result.error)
    }
}
