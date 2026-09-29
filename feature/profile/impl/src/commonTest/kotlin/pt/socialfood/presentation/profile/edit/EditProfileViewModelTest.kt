package pt.socialfood.presentation.profile.edit

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.domain.model.PresignedUrlData
import pt.socialfood.domain.model.User
import pt.socialfood.fakes.FakeImageCache
import pt.socialfood.fakes.FakeUploadPhotoUseCase
import pt.socialfood.fakes.FakeUsersRepository
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

    private fun fakeUsersRepository(
        getUserMeResult: Result<User> = Result.Success(sampleUser),
        updateResult: Result<User> = Result.Success(sampleUser),
        updatePhotoResult: Result<Boolean> = Result.Success(true),
        getPresignedUrlResult: Result<PresignedUrlData> = Result.Success(presignedUrlData),
    ) = FakeUsersRepository(
        getUserMeResult = getUserMeResult,
        updateResult = updateResult,
        updatePhotoResult = updatePhotoResult,
        getPresignedUrlResult = getPresignedUrlResult,
    )

    private fun createViewModel(
        usersRepository: FakeUsersRepository = fakeUsersRepository(),
        uploadPhoto: FakeUploadPhotoUseCase = FakeUploadPhotoUseCase(Result.Success(Unit)),
        imageCache: FakeImageCache = FakeImageCache(),
    ) = EditProfileViewModel(
        usersRepository = usersRepository,
        uploadPhoto = uploadPhoto,
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
    fun `given no pending image when save is called then photo is not uploaded to S3`() = runTestWithMainDispatcher {
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
            val uploadPhoto = FakeUploadPhotoUseCase(Result.Failure(DataError.Network(Exception("test error"))))
            val usersRepository = fakeUsersRepository()
            val imageCache = FakeImageCache()
            val vm =
                createViewModel(
                    uploadPhoto = uploadPhoto,
                    usersRepository = usersRepository,
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
                assertEquals(0, usersRepository.updatePhotoInvokeCount)
                assertEquals(false, failed.isSaving)
                assertEquals(false, failed.isUploadingPhoto)
                assertEquals(ErrorCode.NETWORK, failed.saveError)
                assertEquals(emptyList(), imageCache.clearedUrls)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given the user update fails when save is called then saveError is set`() = runTestWithMainDispatcher {
        // Given
        val usersRepository = fakeUsersRepository(
            updateResult = Result.Failure(DataError.Network(Exception("test error"))),
        )
        val vm = createViewModel(usersRepository = usersRepository)

        vm.state.test {
            assertEquals(EditProfileUiState.Loading, awaitItem())
            assertIs<EditProfileUiState.Loaded>(awaitItem())

            // When
            vm.save()

            // Then
            assertIs<EditProfileUiState.Loaded>(awaitItem()).let { assertEquals(true, it.isSaving) }
            val failed = assertIs<EditProfileUiState.Loaded>(awaitItem())
            assertEquals(false, failed.isSaving)
            assertEquals(ErrorCode.NETWORK, failed.saveError)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given saveError is true when dismissSaveError is called then saveError is cleared`() =
        runTestWithMainDispatcher {
            // Given
            val usersRepository = fakeUsersRepository(
                updateResult = Result.Failure(DataError.Network(Exception("test error"))),
            )
            val vm = createViewModel(usersRepository = usersRepository)

            vm.state.test {
                assertEquals(EditProfileUiState.Loading, awaitItem())
                assertIs<EditProfileUiState.Loaded>(awaitItem())

                vm.save()
                awaitItem() // isSaving = true
                assertIs<EditProfileUiState.Loaded>(awaitItem()).let { assertEquals(ErrorCode.NETWORK, it.saveError) }

                // When
                vm.dismissSaveError()

                // Then
                assertIs<EditProfileUiState.Loaded>(awaitItem()).let { assertEquals(null, it.saveError) }

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given getUserMe fails when created then state is Error`() = runTestWithMainDispatcher {
        // Given
        val vm = createViewModel(
            usersRepository = fakeUsersRepository(
                getUserMeResult = Result.Failure(DataError.Network(Exception("test error"))),
            ),
        )

        // When / Then
        vm.state.test {
            assertEquals(EditProfileUiState.Loading, awaitItem())
            assertEquals(EditProfileUiState.Error(ErrorCode.NETWORK), awaitItem())
        }
    }

    @Test
    fun `given getUserMe succeeds when created then the Loaded state has the user email`() = runTestWithMainDispatcher {
        // Given
        val vm = createViewModel(usersRepository = fakeUsersRepository(getUserMeResult = Result.Success(sampleUser)))

        // When / Then
        vm.state.test {
            assertEquals(EditProfileUiState.Loading, awaitItem())
            val loaded = assertIs<EditProfileUiState.Loaded>(awaitItem())
            assertEquals(sampleUser.email, loaded.email)
        }
    }

    @Test
    fun `given a loaded state when retry is called then reloads the user`() = runTestWithMainDispatcher {
        // Given
        val usersRepository = fakeUsersRepository(getUserMeResult = Result.Success(sampleUser))
        val vm = createViewModel(usersRepository = usersRepository)

        vm.state.test {
            assertEquals(EditProfileUiState.Loading, awaitItem())
            assertIs<EditProfileUiState.Loaded>(awaitItem())

            // When
            vm.retry()

            // Then
            assertEquals(EditProfileUiState.Loading, awaitItem())
            assertIs<EditProfileUiState.Loaded>(awaitItem())
        }
        assertEquals(2, usersRepository.getUserMeInvokeCount)
    }

    @Test
    fun `given field change functions are called then the Loaded state reflects the new values`() =
        runTestWithMainDispatcher {
            // Given
            val vm = createViewModel()

            vm.state.test {
                assertEquals(EditProfileUiState.Loading, awaitItem())
                assertIs<EditProfileUiState.Loaded>(awaitItem())

                // When / Then
                vm.onNameChange("New Name")
                assertEquals("New Name", assertIs<EditProfileUiState.Loaded>(awaitItem()).name)

                vm.onUsernameChange("newusername")
                assertEquals("newusername", assertIs<EditProfileUiState.Loaded>(awaitItem()).username)

                vm.onFacebookUrlChange("https://facebook.com/new")
                assertEquals(
                    "https://facebook.com/new",
                    assertIs<EditProfileUiState.Loaded>(awaitItem()).facebookUrl,
                )

                vm.onInstagramUrlChange("https://instagram.com/new")
                assertEquals(
                    "https://instagram.com/new",
                    assertIs<EditProfileUiState.Loaded>(awaitItem()).instagramUrl,
                )

                vm.onYoutubeUrlChange("https://youtube.com/new")
                assertEquals(
                    "https://youtube.com/new",
                    assertIs<EditProfileUiState.Loaded>(awaitItem()).youtubeUrl,
                )

                vm.onAuthorModeChange(true)
                assertEquals(true, assertIs<EditProfileUiState.Loaded>(awaitItem()).isAuthor)
            }
        }

    @Test
    fun `given author mode toggled on when save is called then isAuthor true is sent`() = runTestWithMainDispatcher {
        // Given
        val usersRepository = fakeUsersRepository(updateResult = Result.Success(sampleUser))
        val vm = createViewModel(usersRepository = usersRepository)

        vm.state.test {
            assertEquals(EditProfileUiState.Loading, awaitItem())
            assertIs<EditProfileUiState.Loaded>(awaitItem())

            // When
            vm.onAuthorModeChange(true)
            awaitItem()
            vm.save()

            // Then
            assertIs<EditProfileUiState.Loaded>(awaitItem()).let { assertEquals(true, it.isSaving) }
            assertIs<EditProfileUiState.Loaded>(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertEquals(1, usersRepository.updateInvokeCount)
        assertEquals(true, usersRepository.lastUpdateIsAuthor)
    }

    @Test
    fun `given getPresignedUrl fails when save is called with a pending photo then saveError is set`() =
        runTestWithMainDispatcher {
            // Given
            val usersRepository = fakeUsersRepository(
                getPresignedUrlResult = Result.Failure(DataError.Network(Exception("test error"))),
            )
            val uploadPhoto = FakeUploadPhotoUseCase(Result.Success(Unit))
            val vm = createViewModel(usersRepository = usersRepository, uploadPhoto = uploadPhoto)

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
                assertEquals(1, usersRepository.getPresignedUrlInvokeCount)
                assertEquals(0, uploadPhoto.invokeCount)
                assertEquals(false, failed.isSaving)
                assertEquals(false, failed.isUploadingPhoto)
                assertEquals(ErrorCode.NETWORK, failed.saveError)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given already saving when save is called again then the second call is ignored`() = runTestWithMainDispatcher {
        // Given
        val usersRepository = fakeUsersRepository(updateResult = Result.Success(sampleUser))
        val vm = createViewModel(usersRepository = usersRepository)

        vm.state.test {
            assertEquals(EditProfileUiState.Loading, awaitItem())
            assertIs<EditProfileUiState.Loaded>(awaitItem())

            // When
            vm.save()
            assertIs<EditProfileUiState.Loaded>(awaitItem()).let { assertEquals(true, it.isSaving) }
            vm.save()

            // Then
            assertIs<EditProfileUiState.Loaded>(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertEquals(1, usersRepository.updateInvokeCount)
    }
}
