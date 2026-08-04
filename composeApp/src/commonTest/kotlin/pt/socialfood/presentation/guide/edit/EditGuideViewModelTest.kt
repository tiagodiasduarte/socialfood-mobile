package pt.socialfood.presentation.guide.edit

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.fakes.FakeDeleteGuideUseCase
import pt.socialfood.fakes.FakeGetGuideByIdUseCase
import pt.socialfood.fakes.FakeGuidesRepository
import pt.socialfood.fakes.FakeUpdateGuideUseCase
import pt.socialfood.fakes.FakeUploadPhotoUseCase
import pt.socialfood.runner.runTestWithMainDispatcher
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.edit_guide_details_description_error
import socialfood.composeapp.generated.resources.edit_guide_details_public_image_warning
import socialfood.composeapp.generated.resources.edit_guide_details_public_restaurants_warning
import socialfood.composeapp.generated.resources.edit_guide_details_title_error
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class EditGuideViewModelTest {
    private fun guide(
        visibility: GuideVisibility = GuideVisibility.PRIVATE,
        restaurants: List<Restaurant> = emptyList(),
    ) = Guide(
        id = "guide-1",
        name = "My Guide",
        description = "My description",
        visibility = visibility,
        author = Author(id = "author-1", name = "Author", username = "author"),
        numberOfRestaurant = restaurants.size,
        restaurants = restaurants,
    )

    private fun restaurant(id: String) = Restaurant(
        id = id,
        name = "Restaurant $id",
        description = "",
        city = "Lisbon",
        country = "Portugal",
        countryCode = "PT",
        postalCode = "1000-000",
        photoNames = emptyList(),
        address = "Rua Augusta 1",
        rating = 4.5,
        userRatingCount = 100,
        websiteUrl = null,
        phoneNumber = "+351910000000",
    )

    private fun createViewModel(
        getGuideById: FakeGetGuideByIdUseCase = FakeGetGuideByIdUseCase(Result.Success(guide())),
        updateGuide: FakeUpdateGuideUseCase = FakeUpdateGuideUseCase(Result.Success(guide())),
        uploadPhoto: FakeUploadPhotoUseCase = FakeUploadPhotoUseCase(Result.Success(Unit)),
        guidesRepository: FakeGuidesRepository = FakeGuidesRepository(),
        deleteGuide: FakeDeleteGuideUseCase = FakeDeleteGuideUseCase(Result.Success(true)),
    ) = EditGuideViewModel(
        getGuideById = getGuideById,
        updateGuide = updateGuide,
        uploadPhoto = uploadPhoto,
        guidesRepository = guidesRepository,
        deleteGuide = deleteGuide,
        guideId = "guide-1",
    )

    @Test
    fun `given getGuideById succeeds when created then state is Loaded`() = runTestWithMainDispatcher {
        // Given / When
        val vm = createViewModel(getGuideById = FakeGetGuideByIdUseCase(Result.Success(guide())))

        // Then
        vm.state.test {
            assertEquals(EditGuideUiState.Loading, awaitItem())
            val state = assertIs<EditGuideUiState.Loaded>(awaitItem())
            assertEquals("My Guide", state.title)
            assertEquals("My description", state.description)
        }
    }

    @Test
    fun `given getGuideById fails when created then state is Error`() = runTestWithMainDispatcher {
        // Given
        val getGuideById = FakeGetGuideByIdUseCase(Result.Failure(DataError.Network(Exception("test error"))))

        // When
        val vm = createViewModel(getGuideById = getGuideById)

        // Then
        vm.state.test {
            assertEquals(EditGuideUiState.Loading, awaitItem())
            assertEquals(EditGuideUiState.Error(ErrorCode.NETWORK), awaitItem())
        }
    }

    @Test
    fun `given a loaded guide when onRetry is called then reloads the guide`() = runTestWithMainDispatcher {
        // Given
        val getGuideById = FakeGetGuideByIdUseCase(Result.Success(guide()))
        val vm = createViewModel(getGuideById = getGuideById)
        vm.state.test {
            skipItems(2)

            // When
            vm.onRetry()

            // Then
            assertEquals(EditGuideUiState.Loading, awaitItem())
            assertIs<EditGuideUiState.Loaded>(awaitItem())
        }
        assertEquals(2, getGuideById.invokeCount)
    }

    @Test
    fun `given a blank title when onSave is called then validationErrors includes title error`() =
        runTestWithMainDispatcher {
            // Given
            val updateGuide = FakeUpdateGuideUseCase(Result.Success(guide()))
            val vm = createViewModel(updateGuide = updateGuide)

            advanceUntilIdle()
            advanceUntilIdle()

            vm.onTitleChange("")

            // When
            vm.onSave()

            // Then
            val state = assertIs<EditGuideUiState.Loaded>(vm.state.value)
            assertTrue(state.titleError)
            assertEquals(listOf(Res.string.edit_guide_details_title_error), state.validationErrors)
            assertEquals(0, updateGuide.invokeCount)
        }

    @Test
    fun `given a blank description when onSave is called then validationErrors includes description error`() =
        runTestWithMainDispatcher {
            // Given
            val updateGuide = FakeUpdateGuideUseCase(Result.Success(guide()))
            val vm = createViewModel(updateGuide = updateGuide)

            advanceUntilIdle()
            advanceUntilIdle()

            vm.onDescriptionChange("")

            // When
            vm.onSave()

            // Then
            val state = assertIs<EditGuideUiState.Loaded>(vm.state.value)
            assertTrue(state.descriptionError)
            assertEquals(listOf(Res.string.edit_guide_details_description_error), state.validationErrors)
            assertEquals(0, updateGuide.invokeCount)
        }

    @Test
    fun `given public visibility missing requirements when onSave is called then validationErrors includes warnings`() =
        runTestWithMainDispatcher {
            // Given
            val updateGuide = FakeUpdateGuideUseCase(Result.Success(guide()))
            val vm = createViewModel(
                getGuideById = FakeGetGuideByIdUseCase(Result.Success(guide(visibility = GuideVisibility.PUBLIC))),
                updateGuide = updateGuide,
            )

            advanceUntilIdle()
            advanceUntilIdle()

            // When
            vm.onSave()

            // Then
            val state = assertIs<EditGuideUiState.Loaded>(vm.state.value)
            assertTrue(Res.string.edit_guide_details_public_restaurants_warning in state.validationErrors)
            assertTrue(Res.string.edit_guide_details_public_image_warning in state.validationErrors)
            assertEquals(0, updateGuide.invokeCount)
        }

    @Test
    fun `given onDismissErrors is called then validationErrors is cleared`() = runTestWithMainDispatcher {
        // Given
        val vm = createViewModel(
            getGuideById = FakeGetGuideByIdUseCase(Result.Success(guide(visibility = GuideVisibility.PUBLIC))),
        )

        advanceUntilIdle()
        advanceUntilIdle()

        vm.onSave()
        assertTrue(assertIs<EditGuideUiState.Loaded>(vm.state.value).validationErrors.isNotEmpty())

        // When
        vm.onDismissErrors()

        // Then
        assertTrue(assertIs<EditGuideUiState.Loaded>(vm.state.value).validationErrors.isEmpty())
    }

    @Test
    fun `given a valid loaded guide when onSave succeeds then NavigateBack event is emitted`() =
        runTestWithMainDispatcher {
            // Given
            val updateGuide = FakeUpdateGuideUseCase(Result.Success(guide()))
            val vm = createViewModel(updateGuide = updateGuide)

            advanceUntilIdle()
            advanceUntilIdle()

            vm.events.test {
                // When
                vm.onSave()

                // Then
                assertEquals(EditGuideViewModel.UiEvent.NavigateBack, awaitItem())
            }
            assertEquals(1, updateGuide.invokeCount)
        }

    @Test
    fun `given updateGuide fails when onSave is called then isSaving is reset`() = runTestWithMainDispatcher {
        // Given
        val updateGuide = FakeUpdateGuideUseCase(Result.Failure(DataError.Network(Exception("test error"))))
        val vm = createViewModel(updateGuide = updateGuide)
        vm.state.test {
            skipItems(2)

            // When
            vm.onSave()

            // Then
            assertTrue(assertIs<EditGuideUiState.Loaded>(awaitItem()).isSaving)
            assertEquals(false, assertIs<EditGuideUiState.Loaded>(awaitItem()).isSaving)
        }
    }

    @Test
    fun `given a pending image when onSave succeeds then photo is uploaded and imageUrl is updated`() =
        runTestWithMainDispatcher {
            // Given
            val uploadPhoto = FakeUploadPhotoUseCase(Result.Success(Unit))
            val guidesRepository = FakeGuidesRepository()
            val updateGuide = FakeUpdateGuideUseCase(Result.Success(guide()))
            val vm = createViewModel(
                updateGuide = updateGuide,
                uploadPhoto = uploadPhoto,
                guidesRepository = guidesRepository,
            )
            advanceUntilIdle()
            advanceUntilIdle()

            vm.onPhotoSelected(byteArrayOf(1, 2, 3), "image/png")

            vm.events.test {
                // When
                vm.onSave()

                // Then
                assertEquals(EditGuideViewModel.UiEvent.NavigateBack, awaitItem())
            }
            assertEquals(1, uploadPhoto.invokeCount)
            assertEquals(1, guidesRepository.addPhotoInvokeCount)
        }

    @Test
    fun `given onDelete succeeds then GuideDeleted event is emitted`() = runTestWithMainDispatcher {
        // Given
        val deleteGuide = FakeDeleteGuideUseCase(Result.Success(true))
        val vm = createViewModel(deleteGuide = deleteGuide)

        advanceUntilIdle()
        advanceUntilIdle()

        vm.events.test {
            // When
            vm.onDelete()

            // Then
            assertEquals(EditGuideViewModel.UiEvent.GuideDeleted, awaitItem())
        }
        assertEquals(1, deleteGuide.invokeCount)
    }

    @Test
    fun `given onDelete fails then isDeleting is reset`() = runTestWithMainDispatcher {
        // Given
        val deleteGuide = FakeDeleteGuideUseCase(Result.Failure(DataError.Network(Exception("test error"))))
        val vm = createViewModel(deleteGuide = deleteGuide)
        vm.state.test {
            skipItems(2)

            // When
            vm.onDelete()

            // Then
            assertTrue(assertIs<EditGuideUiState.Loaded>(awaitItem()).isDeleting)
            assertEquals(false, assertIs<EditGuideUiState.Loaded>(awaitItem()).isDeleting)
        }
    }

    @Test
    fun `given a restaurant when onRestaurantAdded is called then restaurant is added to the list`() =
        runTestWithMainDispatcher {
            // Given
            val vm = createViewModel()
            vm.state.test {
                skipItems(2)

                // When
                vm.onRestaurantAdded(restaurant("r1"))

                // Then
                val state = assertIs<EditGuideUiState.Loaded>(awaitItem())
                assertEquals(listOf("r1"), state.restaurants.map { it.id })
            }
        }

    @Test
    fun `given an already added restaurant when onRestaurantAdded is called then it is not duplicated`() =
        runTestWithMainDispatcher {
            // Given
            val vm = createViewModel(
                getGuideById = FakeGetGuideByIdUseCase(Result.Success(guide(restaurants = listOf(restaurant("r1"))))),
            )
            vm.state.test {
                skipItems(2)

                // When
                vm.onRestaurantAdded(restaurant("r1"))

                // Then
                expectNoEvents()
            }
        }

    @Test
    fun `given a restaurant in the list when onRestaurantRemoved is called then it is removed`() =
        runTestWithMainDispatcher {
            // Given
            val vm = createViewModel(
                getGuideById = FakeGetGuideByIdUseCase(Result.Success(guide(restaurants = listOf(restaurant("r1"))))),
            )
            vm.state.test {
                skipItems(2)

                // When
                vm.onRestaurantRemoved("r1")

                // Then
                val state = assertIs<EditGuideUiState.Loaded>(awaitItem())
                assertTrue(state.restaurants.isEmpty())
            }
        }
}
