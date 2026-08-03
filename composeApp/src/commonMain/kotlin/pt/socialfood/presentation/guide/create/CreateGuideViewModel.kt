package pt.socialfood.presentation.guide.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.error.ErrorEntity
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.repository.GuidesRepository
import pt.socialfood.domain.use_case.guide.CreateGuideUseCase
import pt.socialfood.domain.use_case.photo.UploadPhotoUseCase

class CreateGuideViewModel(
    private val createGuide: CreateGuideUseCase,
    private val uploadPhoto: UploadPhotoUseCase,
    private val guidesRepository: GuidesRepository,
) : ViewModel() {

    val restaurants = MutableStateFlow<List<Restaurant>>(emptyList())

    private val _state = MutableStateFlow<CreateGuideUiState>(CreateGuideUiState.Idle())
    val state: StateFlow<CreateGuideUiState> = _state

    private val _events = MutableSharedFlow<UiEvent>()
    val events = _events.asSharedFlow()

    private fun updateIdle(update: CreateGuideUiState.Idle.() -> CreateGuideUiState.Idle) {
        _state.update { current ->
            val idle = current as? CreateGuideUiState.Idle ?: return@update current
            idle.update()
        }
    }

    fun onTitleChange(value: String) {
        updateIdle { copy(title = value, titleError = titleError && value.isBlank()) }
    }

    fun onDescriptionChange(value: String) {
        updateIdle { copy(description = value, descriptionError = descriptionError && value.isBlank()) }
    }

    fun onPhotoSelected(bytes: ByteArray, mimeType: String) {
        updateIdle { copy(pendingImage = bytes to mimeType) }
    }

    fun onDismissErrors() {
        updateIdle { copy(validationErrors = emptyList()) }
    }

    @OptIn(ExperimentalTime::class)
    fun onCreateGuide() {
        val idle = _state.value as? CreateGuideUiState.Idle ?: return

        val errors = buildList {
            if (idle.title.isBlank()) add(ErrorEntity.Validation.EmptyTitle)
            if (idle.description.isBlank()) add(ErrorEntity.Validation.EmptyDescription)
        }
        if (errors.isNotEmpty()) {
            updateIdle {
                copy(
                    titleError = ErrorEntity.Validation.EmptyTitle in errors,
                    descriptionError = ErrorEntity.Validation.EmptyDescription in errors,
                    validationErrors = errors,
                )
            }
            return
        }

        viewModelScope.launch {
            _state.value = CreateGuideUiState.Loading

            val guide = when (val result = createGuide(
                title = idle.title,
                description = idle.description,
                visibility = GuideVisibility.PRIVATE,
                restaurantIds = emptyList(),
            )) {
                is Result.Success -> result.data
                is Result.Failure -> { _state.value = CreateGuideUiState.Error; return@launch }
            }

            if (idle.pendingImage != null) {
                val (bytes, mimeType) = idle.pendingImage
                val ext = when (mimeType) {
                    "image/png" -> "png"
                    "image/webp" -> "webp"
                    else -> "jpg"
                }
                val fileName = "photo_${Clock.System.now().toEpochMilliseconds()}.$ext"
                val presigned = guidesRepository.getPhotoPresignedUrl(guide.id, fileName, mimeType)
                if (presigned is Result.Success) {
                    if (uploadPhoto(presigned.data, bytes, mimeType) is Result.Success) {
                        guidesRepository.addPhoto(guide.id, presigned.data.publicUrl)
                    }
                }
            }

            _events.emit(UiEvent.GuideCreated(guide.id))
        }
    }

    sealed class UiEvent {
        data class GuideCreated(val guideId: String) : UiEvent()
    }
}
