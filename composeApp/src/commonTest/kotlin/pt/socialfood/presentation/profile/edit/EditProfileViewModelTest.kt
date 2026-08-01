package pt.socialfood.presentation.profile.edit

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import pt.socialfood.core.Result
import pt.socialfood.domain.error.ErrorEntity
import pt.socialfood.domain.model.PresignedUrlData
import pt.socialfood.domain.model.User
import pt.socialfood.fakes.FakeGetPresignedUrlUseCase
import pt.socialfood.fakes.FakeGetUserMeUseCase
import pt.socialfood.fakes.FakeImageCache
import pt.socialfood.fakes.FakeUpdateUserPhotoUseCase
import pt.socialfood.fakes.FakeUpdateUserUseCase
import pt.socialfood.fakes.FakeUploadPhotoUseCase
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class EditProfileViewModelTest {
    private val sampleUser =
        User(
            id = "user-1",
            email = "user@test.com",
            name = "Jane Doe",
            username = "janedoe",
            imageUrl = "https://cdn.socialfood.pt/old.png",
        )

    private val presignedUrlData =
        PresignedUrlData(
            uploadUrl = "https://bucket.s3.amazonaws.com/upload-url",
            publicUrl = "https://cdn.socialfood.pt/new.png",
        )

    private fun createViewModel(
        getPresignedUrl: FakeGetPresignedUrlUseCase = FakeGetPresignedUrlUseCase(Result.Success(presignedUrlData)),
        uploadPhoto: FakeUploadPhotoUseCase = FakeUploadPhotoUseCase(Result.Success(Unit)),
        updateUserPhoto: FakeUpdateUserPhotoUseCase = FakeUpdateUserPhotoUseCase(Result.Success(true)),
        updateUser: FakeUpdateUserUseCase = FakeUpdateUserUseCase(Result.Success(sampleUser)),
        imageCache: FakeImageCache = FakeImageCache(),
    ) = EditProfileViewModel(
        getUserMe = FakeGetUserMeUseCase(Result.Success(sampleUser)),
        updateUser = updateUser,
        uploadPhoto = uploadPhoto,
        updateUserPhoto = updateUserPhoto,
        getPresignedUrl = getPresignedUrl,
        imageCache = imageCache,
    )

    @Test
    fun `given a pending image when save is called then photo is uploaded to S3 before saving`() =
        runTestWithMainDispatcher {
            // Given
            val uploadPhoto = FakeUploadPhotoUseCase(Result.Success(Unit))
            val imageCache = FakeImageCache()
            val vm = createViewModel(uploadPhoto = uploadPhoto, imageCache = imageCache)

            vm.state.test {
                assertEquals(EditProfileUiState.Loading, awaitItem())
                assertIs<EditProfileUiState.Loaded>(awaitItem())

                // When
                vm.onPhotoSelected(byteArrayOf(1, 2, 3), "image/png")
                awaitItem() // pendingImage set

                vm.save()

                // Then
                assertIs<EditProfileUiState.Loaded>(awaitItem()).let { assertEquals(true, it.isSaving) }
                assertIs<EditProfileUiState.Loaded>(awaitItem()).let { assertEquals(true, it.isUploadingPhoto) }

                val photoUploaded = assertIs<EditProfileUiState.Loaded>(awaitItem())
                assertEquals(1, uploadPhoto.invokeCount)
                assertEquals(presignedUrlData, uploadPhoto.lastPresigned)
                assertEquals(presignedUrlData.publicUrl, photoUploaded.imageUrl)
                assertEquals(null, photoUploaded.pendingImage)
                assertEquals(listOf(presignedUrlData.publicUrl), imageCache.clearedUrls)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given no pending image when save is called then photo is not uploaded to S3`() =
        runTestWithMainDispatcher {
            // Given
            val uploadPhoto = FakeUploadPhotoUseCase(Result.Success(Unit))
            val vm = createViewModel(uploadPhoto = uploadPhoto)

            vm.state.test {
                assertEquals(EditProfileUiState.Loading, awaitItem())
                assertIs<EditProfileUiState.Loaded>(awaitItem())

                // When
                vm.save()

                // Then
                cancelAndIgnoreRemainingEvents()
                assertEquals(0, uploadPhoto.invokeCount)
            }
        }

    @Test
    fun `given the S3 upload fails when save is called then the user photo is not updated`() =
        runTestWithMainDispatcher {
            // Given
            val uploadPhoto = FakeUploadPhotoUseCase(Result.Error(ErrorEntity.Unknown))
            val updateUserPhoto = FakeUpdateUserPhotoUseCase(Result.Success(true))
            val imageCache = FakeImageCache()
            val vm =
                createViewModel(
                    uploadPhoto = uploadPhoto,
                    updateUserPhoto = updateUserPhoto,
                    imageCache = imageCache,
                )

            vm.state.test {
                assertEquals(EditProfileUiState.Loading, awaitItem())
                assertIs<EditProfileUiState.Loaded>(awaitItem())

                // When
                vm.onPhotoSelected(byteArrayOf(1, 2, 3), "image/png")
                awaitItem() // pendingImage set
                vm.save()

                // Then
                assertIs<EditProfileUiState.Loaded>(awaitItem()).let { assertEquals(true, it.isSaving) }
                assertIs<EditProfileUiState.Loaded>(awaitItem()).let { assertEquals(true, it.isUploadingPhoto) }

                val failed = assertIs<EditProfileUiState.Loaded>(awaitItem())
                assertEquals(1, uploadPhoto.invokeCount)
                assertEquals(0, updateUserPhoto.invokeCount)
                assertEquals(false, failed.isSaving)
                assertEquals(false, failed.isUploadingPhoto)
                assertEquals(emptyList(), imageCache.clearedUrls)

                cancelAndIgnoreRemainingEvents()
            }
        }
}
