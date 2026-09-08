package pt.socialfood.data.repository

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.data.paging.GuideCacheTransactionRunner
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.domain.model.PresignedUrlData
import pt.socialfood.fakes.FakeGuideDao
import pt.socialfood.fakes.FakeGuideRemoteKeyDao
import pt.socialfood.fakes.FakeGuidesApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class GuidesRepositoryImplTest {
    private fun createRepository(shouldThrow: Boolean = false): GuidesRepositoryImpl = GuidesRepositoryImpl(
        guideApi = FakeGuidesApi(shouldThrow),
        guideDao = FakeGuideDao(),
        guideRemoteKeyDao = FakeGuideRemoteKeyDao(),
        transactionRunner = GuideCacheTransactionRunner { it() },
    )

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
        assertIs<Result.Failure>(result)
        assertIs<DataError.Network>(result.error)
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
        assertIs<Result.Failure>(result)
        assertIs<DataError.Network>(result.error)
    }

    // update

    @Test
    fun `given valid params when update is called then returns Success with updated Guide`() = runTest {
        // Given
        val repo = createRepository()

        // When
        val result =
            repo.update(
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
        val result =
            repo.update(
                id = "guide-id",
                name = "Updated Name",
                userId = "user-id",
                description = "Updated Description",
                restaurantIds = emptyList(),
                visibility = GuideVisibility.PUBLIC,
            )

        // Then
        assertIs<Result.Failure>(result)
        assertIs<DataError.Network>(result.error)
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
        assertIs<Result.Failure>(result)
        assertIs<DataError.Network>(result.error)
    }

    @Test
    fun `given a cached guide when findById is called again then returns cache without hitting api`() = runTest {
        // Given
        val api = FakeGuidesApi()
        val repo = GuidesRepositoryImpl(
            guideApi = api,
            guideDao = FakeGuideDao(),
            guideRemoteKeyDao = FakeGuideRemoteKeyDao(),
            transactionRunner = GuideCacheTransactionRunner { it() },
        )
        repo.findById(id = "guide-id")

        // When
        val result = repo.findById(id = "guide-id")

        // Then
        assertIs<Result.Success<Guide>>(result)
        assertEquals(1, api.findByIdCallCount)
    }

    @Test
    fun `given a cached guide when update is called then findById returns fresh data without hitting api`() = runTest {
        // Given
        val api = FakeGuidesApi()
        val repo = GuidesRepositoryImpl(
            guideApi = api,
            guideDao = FakeGuideDao(),
            guideRemoteKeyDao = FakeGuideRemoteKeyDao(),
            transactionRunner = GuideCacheTransactionRunner { it() },
        )
        repo.findById(id = "guide-id")

        // When
        repo.update(
            id = "guide-id",
            name = "Updated Name",
            userId = "user-id",
            description = "Updated Description",
            restaurantIds = emptyList(),
            visibility = GuideVisibility.PUBLIC,
        )
        val result = repo.findById(id = "guide-id")

        // Then
        assertIs<Result.Success<Guide>>(result)
        assertEquals(1, api.findByIdCallCount)
    }

    @Test
    fun `given a cached guide when delete is called then findById hits api again`() = runTest {
        // Given
        val api = FakeGuidesApi()
        val repo = GuidesRepositoryImpl(
            guideApi = api,
            guideDao = FakeGuideDao(),
            guideRemoteKeyDao = FakeGuideRemoteKeyDao(),
            transactionRunner = GuideCacheTransactionRunner { it() },
        )
        repo.findById(id = "guide-id")

        // When
        repo.delete(id = "guide-id")
        repo.findById(id = "guide-id")

        // Then
        assertEquals(2, api.findByIdCallCount)
    }

    // getPhotoPresignedUrl

    @Test
    fun `given valid params when getPhotoPresignedUrl is called then returns Success with PresignedUrlData`() =
        runTest {
            // Given
            val repo = createRepository()

            // When
            val result =
                repo.getPhotoPresignedUrl(
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
        val result =
            repo.getPhotoPresignedUrl(
                guideId = "guide-id",
                fileName = "photo.jpg",
                mimeType = "image/jpeg",
            )

        // Then
        assertIs<Result.Failure>(result)
        assertIs<DataError.Network>(result.error)
    }

    // addRestaurantGuide

    @Test
    fun `given valid params when addRestaurantGuide is called then returns Success with Guide`() = runTest {
        // Given
        val repo = createRepository()

        // When
        val result =
            repo.addRestaurantGuide(
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
        val result =
            repo.addRestaurantGuide(
                guideId = "guide-id",
                userId = "user-id",
                placeId = "place-id",
            )

        // Then
        assertIs<Result.Failure>(result)
        assertIs<DataError.Network>(result.error)
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
        assertIs<Result.Failure>(result)
        assertIs<DataError.Network>(result.error)
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
        assertIs<Result.Failure>(result)
        assertIs<DataError.Network>(result.error)
    }

    // findGuidesPagingFlow

    @Test
    fun `given findGuidesPagingFlow is called then Pager is configured with a RemoteMediator scoped to all`() =
        runTest {
            // Given
            val repo = createRepository()

            // When
            val flow = repo.findGuidesPagingFlow()

            // Then
            assertNotNull(flow)
        }

    // findUserGuidesPagingFlow

    @Test
    fun `given findUserGuidesPagingFlow is called then Pager is configured with a RemoteMediator scoped to userId`() =
        runTest {
            // Given
            val repo = createRepository()

            // When
            val scopedFlow = repo.findUserGuidesPagingFlow(userId = "user-1")

            // Then
            assertNotNull(scopedFlow)
        }

    // findUserJoinedGuidesPagingFlow

    @Test
    fun `given findUserJoinedGuidesPagingFlow is called then Pager is configured with a scoped RemoteMediator`() =
        runTest {
            // Given
            val repo = createRepository()

            // When
            val joinedFlow = repo.findUserJoinedGuidesPagingFlow(userId = "user-1")

            // Then
            assertNotNull(joinedFlow)
        }
}
