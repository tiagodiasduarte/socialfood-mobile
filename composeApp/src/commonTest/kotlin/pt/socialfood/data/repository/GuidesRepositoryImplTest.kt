package pt.socialfood.data.repository

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.ErrorEntity
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.domain.model.PagedGuides
import pt.socialfood.domain.model.PresignedUrlData
import pt.socialfood.fakes.FakeGuidesApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class GuidesRepositoryImplTest {

    private fun createRepository(shouldThrow: Boolean = false): GuidesRepositoryImpl =
        GuidesRepositoryImpl(FakeGuidesApi(shouldThrow))

    // create

    @Test
    fun `given valid params when create is called then returns Success with Guide`() = runTest {
        // Given
        val repo = createRepository()

        // When
        val result = repo.create(name = "Guide Name", description = "Description", userId = "user-id")

        // Then
        assertIs<Result.Success<Guide>>(result)
        assertEquals("guide-id", result.data.id)
        assertEquals("Guide Name", result.data.name)
    }

    @Test
    fun `given api throws when create is called then returns Error Unknown`() = runTest {
        // Given
        val repo = createRepository(shouldThrow = true)

        // When
        val result = repo.create(name = "Guide Name", description = "Description", userId = "user-id")

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
        val result = repo.delete(id = "guide-id")

        // Then
        assertIs<Result.Success<Boolean>>(result)
        assertEquals(true, result.data)
    }

    @Test
    fun `given api throws when delete is called then returns Error Unknown`() = runTest {
        // Given
        val repo = createRepository(shouldThrow = true)

        // When
        val result = repo.delete(id = "guide-id")

        // Then
        assertIs<Result.Error>(result)
        assertEquals(ErrorEntity.Unknown, result.error)
    }

    // findGuides

    @Test
    fun `given guides exist when findGuides is called then returns Success with list`() = runTest {
        // Given
        val repo = createRepository()

        // When
        val result = repo.findGuides()

        // Then
        assertIs<Result.Success<List<Guide>>>(result)
        assertTrue(result.data.isNotEmpty())
        assertEquals("guide-id", result.data.first().id)
    }

    @Test
    fun `given api throws when findGuides is called then returns Error Unknown`() = runTest {
        // Given
        val repo = createRepository(shouldThrow = true)

        // When
        val result = repo.findGuides()

        // Then
        assertIs<Result.Error>(result)
        assertEquals(ErrorEntity.Unknown, result.error)
    }

    // findGuidesPaged

    @Test
    fun `given valid pagination params when findGuidesPaged is called then returns Success with PagedGuides and correct hasMore flag`() = runTest {
        // Given
        val repo = createRepository()
        val page = 1
        val limit = 10
        // FakeGuidesApi returns total = 25, so page * limit = 10 < 25 → hasMore = true

        // When
        val result = repo.findGuidesPaged(page = page, limit = limit, query = null)

        // Then
        assertIs<Result.Success<PagedGuides>>(result)
        assertEquals(page, result.data.page)
        assertEquals(25, result.data.total)
        assertTrue(result.data.hasMore)
    }

    @Test
    fun `given api throws when findGuidesPaged is called then returns Error Unknown`() = runTest {
        // Given
        val repo = createRepository(shouldThrow = true)

        // When
        val result = repo.findGuidesPaged(page = 1, limit = 10, query = null)

        // Then
        assertIs<Result.Error>(result)
        assertEquals(ErrorEntity.Unknown, result.error)
    }

    // update

    @Test
    fun `given valid params when update is called then returns Success with updated Guide`() = runTest {
        // Given
        val repo = createRepository()

        // When
        val result = repo.update(
            id = "guide-id",
            name = "Updated Name",
            userId = "user-id",
            description = "Updated Description",
            restaurantIds = emptyList(),
            visibility = GuideVisibility.PUBLIC,
        )

        // Then
        assertIs<Result.Success<Guide>>(result)
        assertEquals("guide-id", result.data.id)
    }

    @Test
    fun `given api throws when update is called then returns Error Unknown`() = runTest {
        // Given
        val repo = createRepository(shouldThrow = true)

        // When
        val result = repo.update(
            id = "guide-id",
            name = "Updated Name",
            userId = "user-id",
            description = "Updated Description",
            restaurantIds = emptyList(),
            visibility = GuideVisibility.PUBLIC,
        )

        // Then
        assertIs<Result.Error>(result)
        assertEquals(ErrorEntity.Unknown, result.error)
    }

    // findById

    @Test
    fun `given valid id when findById is called then returns Success with Guide`() = runTest {
        // Given
        val repo = createRepository()

        // When
        val result = repo.findById(id = "guide-id")

        // Then
        assertIs<Result.Success<Guide>>(result)
        assertEquals("guide-id", result.data.id)
    }

    @Test
    fun `given api throws when findById is called then returns Error Unknown`() = runTest {
        // Given
        val repo = createRepository(shouldThrow = true)

        // When
        val result = repo.findById(id = "guide-id")

        // Then
        assertIs<Result.Error>(result)
        assertEquals(ErrorEntity.Unknown, result.error)
    }

    // getPhotoPresignedUrl

    @Test
    fun `given valid params when getPhotoPresignedUrl is called then returns Success with PresignedUrlData`() = runTest {
        // Given
        val repo = createRepository()

        // When
        val result = repo.getPhotoPresignedUrl(
            guideId = "guide-id",
            fileName = "photo.jpg",
            mimeType = "image/jpeg",
        )

        // Then
        assertIs<Result.Success<PresignedUrlData>>(result)
        assertEquals("https://upload.example.com/photo", result.data.uploadUrl)
        assertEquals("https://public.example.com/photo", result.data.publicUrl)
    }

    @Test
    fun `given api throws when getPhotoPresignedUrl is called then returns Error Unknown`() = runTest {
        // Given
        val repo = createRepository(shouldThrow = true)

        // When
        val result = repo.getPhotoPresignedUrl(
            guideId = "guide-id",
            fileName = "photo.jpg",
            mimeType = "image/jpeg",
        )

        // Then
        assertIs<Result.Error>(result)
        assertEquals(ErrorEntity.Unknown, result.error)
    }

    // addRestaurantGuide

    @Test
    fun `given valid params when addRestaurantGuide is called then returns Success with Guide`() = runTest {
        // Given
        val repo = createRepository()

        // When
        val result = repo.addRestaurantGuide(
            guideId = "guide-id",
            userId = "user-id",
            placeId = "place-id",
        )

        // Then
        assertIs<Result.Success<Guide>>(result)
        assertEquals("guide-id", result.data.id)
    }

    @Test
    fun `given api throws when addRestaurantGuide is called then returns Error Unknown`() = runTest {
        // Given
        val repo = createRepository(shouldThrow = true)

        // When
        val result = repo.addRestaurantGuide(
            guideId = "guide-id",
            userId = "user-id",
            placeId = "place-id",
        )

        // Then
        assertIs<Result.Error>(result)
        assertEquals(ErrorEntity.Unknown, result.error)
    }

    // addPhoto

    @Test
    fun `given valid params when addPhoto is called then returns Success true`() = runTest {
        // Given
        val repo = createRepository()

        // When
        val result = repo.addPhoto(guideId = "guide-id", imageUrl = "https://example.com/photo.jpg")

        // Then
        assertIs<Result.Success<Boolean>>(result)
        assertEquals(true, result.data)
    }

    @Test
    fun `given api throws when addPhoto is called then returns Error Unknown`() = runTest {
        // Given
        val repo = createRepository(shouldThrow = true)

        // When
        val result = repo.addPhoto(guideId = "guide-id", imageUrl = "https://example.com/photo.jpg")

        // Then
        assertIs<Result.Error>(result)
        assertEquals(ErrorEntity.Unknown, result.error)
    }

    // deletePhoto

    @Test
    fun `given valid guide id when deletePhoto is called then returns Success true`() = runTest {
        // Given
        val repo = createRepository()

        // When
        val result = repo.deletePhoto(guideId = "guide-id")

        // Then
        assertIs<Result.Success<Boolean>>(result)
        assertEquals(true, result.data)
    }

    @Test
    fun `given api throws when deletePhoto is called then returns Error Unknown`() = runTest {
        // Given
        val repo = createRepository(shouldThrow = true)

        // When
        val result = repo.deletePhoto(guideId = "guide-id")

        // Then
        assertIs<Result.Error>(result)
        assertEquals(ErrorEntity.Unknown, result.error)
    }
}
