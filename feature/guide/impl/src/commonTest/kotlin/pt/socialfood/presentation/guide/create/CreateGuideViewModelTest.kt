package pt.socialfood.presentation.guide.create

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.fakes.FakeCreateGuideUseCase
import pt.socialfood.fakes.FakeGuidesRepository
import pt.socialfood.fakes.FakePhotosRepository
import pt.socialfood.feature.guide.impl.generated.resources.Res
import pt.socialfood.feature.guide.impl.generated.resources.edit_guide_details_description_error
import pt.socialfood.feature.guide.impl.generated.resources.edit_guide_details_title_error
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CreateGuideViewModelTest {
    private val sampleGuide =
        Guide(
            id = "guide-1",
            name = "My Guide",
            description = "My description",
            visibility = GuideVisibility.PRIVATE,
            author = Author(id = "author-1", name = "Author", username = "author"),
            numberOfRestaurant = 0,
        )

    private fun createViewModel(
        createGuide: FakeCreateGuideUseCase = FakeCreateGuideUseCase(Result.Success(sampleGuide)),
        photosRepository: FakePhotosRepository = FakePhotosRepository(),
        guidesRepository: FakeGuidesRepository = FakeGuidesRepository(),
    ) = CreateGuideViewModel(
        createGuide = createGuide,
        photosRepository = photosRepository,
        guidesRepository = guidesRepository,
    )

    @Test
    fun `given a new view model when created then state is Idle`() = runTestWithMainDispatcher {
        // Given
        val vm = createViewModel()

        // When / Then
        assertEquals(CreateGuideUiState.Idle(), vm.state.value)
    }

    @Test
    fun `given a blank title when onCreateGuide is called then validationErrors includes title error`() =
        runTestWithMainDispatcher {
            // Given
            val createGuide = FakeCreateGuideUseCase(Result.Success(sampleGuide))
            val vm = createViewModel(createGuide = createGuide)
            vm.onDescriptionChange("My description")

            // When
            vm.onCreateGuide()

            // Then
            val state = assertIs<CreateGuideUiState.Idle>(vm.state.value)
            assertTrue(state.titleError)
            assertEquals(listOf(Res.string.edit_guide_details_title_error), state.validationErrors)
            assertEquals(0, createGuide.invokeCount)
        }

    @Test
    fun `given a blank description when onCreateGuide is called then validationErrors includes description error`() =
        runTestWithMainDispatcher {
            // Given
            val createGuide = FakeCreateGuideUseCase(Result.Success(sampleGuide))
            val vm = createViewModel(createGuide = createGuide)
            vm.onTitleChange("My Guide")

            // When
            vm.onCreateGuide()

            // Then
            val state = assertIs<CreateGuideUiState.Idle>(vm.state.value)
            assertTrue(state.descriptionError)
            assertEquals(listOf(Res.string.edit_guide_details_description_error), state.validationErrors)
            assertEquals(0, createGuide.invokeCount)
        }

    @Test
    fun `given onDismissErrors is called then validationErrors is cleared`() = runTestWithMainDispatcher {
        // Given
        val vm = createViewModel()
        vm.onCreateGuide()
        assertTrue(assertIs<CreateGuideUiState.Idle>(vm.state.value).validationErrors.isNotEmpty())

        // When
        vm.onDismissErrors()

        // Then
        assertTrue(assertIs<CreateGuideUiState.Idle>(vm.state.value).validationErrors.isEmpty())
    }

    @Test
    fun `given valid title and description when onCreateGuide is called then GuideCreated event is emitted`() =
        runTestWithMainDispatcher {
            // Given
            val createGuide = FakeCreateGuideUseCase(Result.Success(sampleGuide))
            val vm = createViewModel(createGuide = createGuide)
            vm.onTitleChange("My Guide")
            vm.onDescriptionChange("My description")

            vm.events.test {
                // When
                vm.onCreateGuide()

                // Then
                val event = assertIs<CreateGuideViewModel.UiEvent.GuideCreated>(awaitItem())
                assertEquals(sampleGuide.id, event.guideId)
            }
            assertEquals(1, createGuide.invokeCount)
        }

    @Test
    fun `given createGuide fails when onCreateGuide is called then state is Error`() = runTestWithMainDispatcher {
        // Given
        val createGuide = FakeCreateGuideUseCase(Result.Failure(DataError.Network(Exception("test error"))))
        val vm = createViewModel(createGuide = createGuide)
        vm.onTitleChange("My Guide")
        vm.onDescriptionChange("My description")

        vm.state.test {
            assertIs<CreateGuideUiState.Idle>(awaitItem())

            // When
            vm.onCreateGuide()

            // Then
            assertEquals(CreateGuideUiState.Loading, awaitItem())
            assertEquals(CreateGuideUiState.Error(ErrorCode.NETWORK), awaitItem())
        }
    }

    @Test
    fun `given a pending image when onCreateGuide succeeds then photo is uploaded`() = runTestWithMainDispatcher {
        // Given
        val createGuide = FakeCreateGuideUseCase(Result.Success(sampleGuide))
        val photosRepository = FakePhotosRepository()
        val guidesRepository = FakeGuidesRepository()
        val vm = createViewModel(
            createGuide = createGuide,
            photosRepository = photosRepository,
            guidesRepository = guidesRepository,
        )
        vm.onTitleChange("My Guide")
        vm.onDescriptionChange("My description")
        vm.onPhotoSelected(byteArrayOf(1, 2, 3), "image/png")

        vm.events.test {
            // When
            vm.onCreateGuide()

            // Then
            val event = assertIs<CreateGuideViewModel.UiEvent.GuideCreated>(awaitItem())
            assertEquals(sampleGuide.id, event.guideId)
        }
        assertEquals(1, photosRepository.uploadInvokeCount)
        assertEquals(1, guidesRepository.addPhotoInvokeCount)
    }

    @Test
    fun `given no pending image when onCreateGuide succeeds then photo is not uploaded`() = runTestWithMainDispatcher {
        // Given
        val createGuide = FakeCreateGuideUseCase(Result.Success(sampleGuide))
        val photosRepository = FakePhotosRepository()
        val guidesRepository = FakeGuidesRepository()
        val vm = createViewModel(
            createGuide = createGuide,
            photosRepository = photosRepository,
            guidesRepository = guidesRepository,
        )
        vm.onTitleChange("My Guide")
        vm.onDescriptionChange("My description")

        vm.events.test {
            // When
            vm.onCreateGuide()

            // Then
            assertIs<CreateGuideViewModel.UiEvent.GuideCreated>(awaitItem())
        }
        assertEquals(0, photosRepository.uploadInvokeCount)
        assertEquals(0, guidesRepository.addPhotoInvokeCount)
    }
}
