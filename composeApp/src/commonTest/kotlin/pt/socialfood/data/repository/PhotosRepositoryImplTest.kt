package pt.socialfood.data.repository

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.ErrorEntity
import pt.socialfood.fakes.FakeCrashReporter
import pt.socialfood.fakes.FakeS3Api
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class PhotosRepositoryImplTest {

    private val crashReporter = FakeCrashReporter()

    @Test
    fun `given valid upload params when uploadToS3 is called then returns Success`() = runTest {
        // Given
        val repo = PhotosRepositoryImpl(FakeS3Api(shouldThrow = false), crashReporter)

        // When
        val result = repo.uploadToS3("https://s3.example.com/key", ByteArray(0), "image/jpeg")

        // Then
        assertIs<Result.Success<Unit>>(result)
    }

    @Test
    fun `given s3Api throws when uploadToS3 is called then returns Error Unknown`() = runTest {
        // Given
        val repo = PhotosRepositoryImpl(FakeS3Api(shouldThrow = true), crashReporter)

        // When
        val result = repo.uploadToS3("https://s3.example.com/key", ByteArray(0), "image/jpeg")

        // Then
        assertIs<Result.Error>(result)
        assertEquals(ErrorEntity.Unknown, result.error)
        assertEquals(1, crashReporter.recordedExceptions.size)
    }
}
