package pt.socialfood.data.repository

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.ErrorEntity
import pt.socialfood.domain.model.PagedUsers
import pt.socialfood.domain.model.PresignedUrlData
import pt.socialfood.domain.model.User
import pt.socialfood.fakes.FakeUserApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UsersRepositoryImplTest {

    private fun createRepository(shouldThrow: Boolean = false): UsersRepositoryImpl =
        UsersRepositoryImpl(FakeUserApi(shouldThrow))

    // getUsers

    @Test
    fun `given users exist when getUsers is called then returns Success with list of User`() = runTest {
        // Given
        val repo = createRepository()

        // When
        val result = repo.getUsers()

        // Then
        assertIs<Result.Success<List<User>>>(result)
        assertTrue(result.data.isNotEmpty())
        assertEquals("user-id", result.data.first().id)
    }

    @Test
    fun `given api throws when getUsers is called then returns Error Unknown`() = runTest {
        // Given
        val repo = createRepository(shouldThrow = true)

        // When
        val result = repo.getUsers()

        // Then
        assertIs<Result.Error>(result)
        assertEquals(ErrorEntity.Unknown, result.error)
    }

    // findUsers

    @Test
    fun `given valid pagination params when findUsers is called then returns Success with PagedUsers and correct hasMore flag`() = runTest {
        // Given
        val repo = createRepository()
        val page = 1
        val limit = 10
        // FakeUserApi returns total = 25, so page * limit = 10 < 25 → hasMore = true

        // When
        val result = repo.findUsers(page = page, limit = limit, query = null)

        // Then
        assertIs<Result.Success<PagedUsers>>(result)
        assertEquals(page, result.data.page)
        assertEquals(25, result.data.total)
        assertTrue(result.data.hasMore)
    }

    @Test
    fun `given api throws when findUsers is called then returns Error Unknown`() = runTest {
        // Given
        val repo = createRepository(shouldThrow = true)

        // When
        val result = repo.findUsers(page = 1, limit = 10, query = null)

        // Then
        assertIs<Result.Error>(result)
        assertEquals(ErrorEntity.Unknown, result.error)
    }

    // getUserMe

    @Test
    fun `given api returns user when getUserMe is called then returns Success with User and updates currentUser`() = runTest {
        // Given
        val repo = createRepository()

        // When
        val result = repo.getUserMe()

        // Then
        assertIs<Result.Success<User>>(result)
        assertEquals("user-id", result.data.id)
        assertNotNull(repo.currentUser.value)
        assertEquals("user-id", repo.currentUser.value?.id)
    }

    @Test
    fun `given api throws when getUserMe is called then returns Error Unknown`() = runTest {
        // Given
        val repo = createRepository(shouldThrow = true)

        // When
        val result = repo.getUserMe()

        // Then
        assertIs<Result.Error>(result)
        assertEquals(ErrorEntity.Unknown, result.error)
    }

    // findById

    @Test
    fun `given valid id when findById is called then returns Success with User`() = runTest {
        // Given
        val repo = createRepository()

        // When
        val result = repo.findById(id = "user-id")

        // Then
        assertIs<Result.Success<User>>(result)
        assertEquals("user-id", result.data.id)
    }

    @Test
    fun `given api throws when findById is called then returns Error Unknown`() = runTest {
        // Given
        val repo = createRepository(shouldThrow = true)

        // When
        val result = repo.findById(id = "user-id")

        // Then
        assertIs<Result.Error>(result)
        assertEquals(ErrorEntity.Unknown, result.error)
    }

    // update

    @Test
    fun `given valid params when update is called then returns Success with updated User and updates currentUser`() = runTest {
        // Given
        val repo = createRepository()

        // When
        val result = repo.update(
            id = "user-id",
            role = "USER",
            imageUrl = null,
            name = "Test User",
            city = "Lisbon",
            country = "Portugal",
            facebookUrl = null,
            instagramUrl = null,
            youtubeUrl = null,
        )

        // Then
        assertIs<Result.Success<User>>(result)
        assertEquals("user-id", result.data.id)
        assertNotNull(repo.currentUser.value)
        assertEquals("user-id", repo.currentUser.value?.id)
    }

    @Test
    fun `given api throws when update is called then returns Error Unknown`() = runTest {
        // Given
        val repo = createRepository(shouldThrow = true)

        // When
        val result = repo.update(
            id = "user-id",
            role = null,
            imageUrl = null,
            name = null,
            city = null,
            country = null,
            facebookUrl = null,
            instagramUrl = null,
            youtubeUrl = null,
        )

        // Then
        assertIs<Result.Error>(result)
        assertEquals(ErrorEntity.Unknown, result.error)
    }

    // updatePhoto

    @Test
    fun `given valid id and imageUrl when updatePhoto is called then returns Success true`() = runTest {
        // Given
        val repo = createRepository()

        // When
        val result = repo.updatePhoto(id = "user-id", imageUrl = "https://example.com/photo.jpg")

        // Then
        assertIs<Result.Success<Boolean>>(result)
        assertEquals(true, result.data)
    }

    @Test
    fun `given api throws when updatePhoto is called then returns Error Unknown`() = runTest {
        // Given
        val repo = createRepository(shouldThrow = true)

        // When
        val result = repo.updatePhoto(id = "user-id", imageUrl = "https://example.com/photo.jpg")

        // Then
        assertIs<Result.Error>(result)
        assertEquals(ErrorEntity.Unknown, result.error)
    }

    // getPresignedUrl

    @Test
    fun `given valid params when getPresignedUrl is called then returns Success with PresignedUrlData`() = runTest {
        // Given
        val repo = createRepository()

        // When
        val result = repo.getPresignedUrl(
            userId = "user-id",
            fileName = "photo.jpg",
            mimeType = "image/jpeg",
            context = "profile",
        )

        // Then
        assertIs<Result.Success<PresignedUrlData>>(result)
        assertEquals("https://upload.example.com/user-photo", result.data.uploadUrl)
        assertEquals("https://public.example.com/user-photo", result.data.publicUrl)
    }

    @Test
    fun `given api throws when getPresignedUrl is called then returns Error Unknown`() = runTest {
        // Given
        val repo = createRepository(shouldThrow = true)

        // When
        val result = repo.getPresignedUrl(
            userId = "user-id",
            fileName = "photo.jpg",
            mimeType = "image/jpeg",
            context = "profile",
        )

        // Then
        assertIs<Result.Error>(result)
        assertEquals(ErrorEntity.Unknown, result.error)
    }

    // saveUser / clearUser / currentUser flow

    @Test
    fun `given a user when saveUser is called then currentUser emits the saved user`() = runTest {
        // Given
        val repo = createRepository()
        val user = User(
            id = "user-id",
            email = "user@test.com",
            name = "Test User",
        )

        // When
        repo.saveUser(user)

        // Then
        assertNotNull(repo.currentUser.value)
        assertEquals("user-id", repo.currentUser.value?.id)
    }

    @Test
    fun `given a saved user when clearUser is called then currentUser emits null`() = runTest {
        // Given
        val repo = createRepository()
        val user = User(
            id = "user-id",
            email = "user@test.com",
            name = "Test User",
        )
        repo.saveUser(user)

        // When
        repo.clearUser()

        // Then
        assertNull(repo.currentUser.value)
    }
}
