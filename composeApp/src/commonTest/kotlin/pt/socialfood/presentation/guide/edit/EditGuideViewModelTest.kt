package pt.socialfood.presentation.guide.edit

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.fakes.FakeDeleteGuideUseCase
import pt.socialfood.fakes.FakeGetGuideByIdUseCase
import pt.socialfood.fakes.FakeGuidesRepository
import pt.socialfood.fakes.FakeUpdateGuideUseCase
import pt.socialfood.fakes.FakeUploadPhotoUseCase
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class EditGuideViewModelTest {
    private val fakeGuide =
        Guide(
            id = "guide-id",
            name = "Guide Name",
            description = "Guide Description",
            visibility = GuideVisibility.PUBLIC,
            author = Author(id = "author-id", name = "Author", username = "author"),
            numberOfRestaurant = 0,
        )

    private fun createViewModel(
        getGuideById: FakeGetGuideByIdUseCase =
            FakeGetGuideByIdUseCase(Result.Failure(DataError.Network(Exception("test error")))),
        updateGuide: FakeUpdateGuideUseCase =
            FakeUpdateGuideUseCase(Result.Failure(DataError.Network(Exception("test error")))),
    ) = EditGuideViewModel(
        getGuideById = getGuideById,
        updateGuide = updateGuide,
        uploadPhoto = FakeUploadPhotoUseCase(Result.Success(Unit)),
        guidesRepository = FakeGuidesRepository(),
        deleteGuide = FakeDeleteGuideUseCase(),
        guideId = fakeGuide.id,
    )

    @Test
    fun `given loading the guide fails when created then state is Error with the backend message`() =
        runTestWithMainDispatcher {
            // Given
            val getGuideById = FakeGetGuideByIdUseCase(Result.Failure(DataError.Network(Exception("test error"))))

            // When / Then
            val vm = createViewModel(getGuideById = getGuideById)
            vm.state.test {
                assertEquals(EditGuideUiState.Loading, awaitItem())
                val error = assertIs<EditGuideUiState.Error>(awaitItem())
                assertEquals("Something went wrong", error.message)
            }
        }

    @Test
    fun `given the guide loads successfully when created then state is Loaded`() = runTestWithMainDispatcher {
        // Given
        val getGuideById = FakeGetGuideByIdUseCase(Result.Success(fakeGuide))

        // When / Then
        val vm = createViewModel(getGuideById = getGuideById)
        vm.state.test {
            assertEquals(EditGuideUiState.Loading, awaitItem())
            val loaded = assertIs<EditGuideUiState.Loaded>(awaitItem())
            assertEquals(fakeGuide.id, loaded.guide.id)
        }
    }
}
