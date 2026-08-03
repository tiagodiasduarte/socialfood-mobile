package pt.socialfood.presentation.guide.create

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.fakes.FakeCreateGuideUseCase
import pt.socialfood.fakes.FakeGuidesRepository
import pt.socialfood.fakes.FakeUploadPhotoUseCase
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class CreateGuideViewModelTest {
    private val fakeGuide =
        Guide(
            id = "guide-id",
            name = "New Guide",
            description = "Description",
            visibility = GuideVisibility.PRIVATE,
            author = Author(id = "author-id", name = "Author", username = "author"),
            numberOfRestaurant = 0,
        )

    private fun createViewModel(
        createGuide: FakeCreateGuideUseCase =
            FakeCreateGuideUseCase(Result.Failure(DataError.Network(Exception("test error")))),
    ) = CreateGuideViewModel(
        createGuide = createGuide,
        uploadPhoto = FakeUploadPhotoUseCase(Result.Success(Unit)),
        guidesRepository = FakeGuidesRepository(),
    )

    @Test
    fun `given creating the guide fails when onCreateGuide is called then state is Error with the backend message`() =
        runTestWithMainDispatcher {
            // Given
            val createGuide = FakeCreateGuideUseCase(Result.Failure(DataError.Network(Exception("test error"))))
            val vm = createViewModel(createGuide = createGuide)
            vm.onTitleChange("Title")
            vm.onDescriptionChange("Description")

            // When / Then
            vm.state.test {
                assertIs<CreateGuideUiState.Idle>(awaitItem())

                vm.onCreateGuide()

                assertEquals(CreateGuideUiState.Loading, awaitItem())
                val error = assertIs<CreateGuideUiState.Error>(awaitItem())
                assertEquals("Something went wrong", error.message)
            }
        }

    @Test
    fun `given creating the guide succeeds when onCreateGuide is called then GuideCreated is emitted`() =
        runTestWithMainDispatcher {
            // Given
            val createGuide = FakeCreateGuideUseCase(Result.Success(fakeGuide))
            val vm = createViewModel(createGuide = createGuide)
            vm.onTitleChange("Title")
            vm.onDescriptionChange("Description")

            // When / Then
            vm.events.test {
                vm.onCreateGuide()

                val event = awaitItem() as CreateGuideViewModel.UiEvent.GuideCreated
                assertEquals(fakeGuide.id, event.guideId)
            }
        }
}
