package pt.socialfood.presentation.guide.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import pt.socialfood.domain.use_case.guide.DeleteGuideUseCase
import pt.socialfood.domain.use_case.guide.GetGuideByIdUseCase
import pt.socialfood.domain.use_case.guide.UpdateGuideUseCase
import pt.socialfood.domain.use_case.photo.UploadPhotoUseCase
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class EditGuideViewModel(
    private val getGuideById: GetGuideByIdUseCase,
    private val updateGuide: UpdateGuideUseCase,
    private val uploadPhoto: UploadPhotoUseCase,
    private val guidesRepository: GuidesRepository,
    private val deleteGuide: DeleteGuideUseCase,
    private val guideId: String,
) : ViewModel() {
    private val _state = MutableStateFlow<EditGuideUiState>(EditGuideUiState.Loading)
    val state: StateFlow<EditGuideUiState> = _state

    private val _events = MutableSharedFlow<UiEvent>()
    val events = _events.asSharedFlow()

    private fun updateLoaded(update: EditGuideUiState.Loaded.() -> EditGuideUiState.Loaded) {
        _state.update { current ->
            val loaded = current as? EditGuideUiState.Loaded ?: return@update current
            loaded.update()
        }
    }

    fun onRestaurantAdded(restaurant: Restaurant) {
        updateLoaded {
            if (restaurants.any { it.id == restaurant.id }) return@updateLoaded this
            copy(restaurants = restaurants + restaurant)
        }
    }

    fun onRestaurantRemoved(placeId: String) {
        updateLoaded { copy(restaurants = restaurants.filter { it.id != placeId }) }
    }

    init {
        loadGuide(guideId)
    }

    private fun loadGuide(id: String) {
        viewModelScope.launch {
            _state.value = EditGuideUiState.Loading

            when (val result = getGuideById(id)) {
                is Result.Success ->
                    _state.value =
                        EditGuideUiState.Loaded(
                            guide = result.data,
                            title = result.data.name,
                            description = result.data.description,
                            visibility = result.data.visibility,
                            restaurants = result.data.restaurants,
                            imageUrl = result.data.imageUrl,
                        )
                is Result.Error -> _state.value = EditGuideUiState.Error
            }
        }
    }

    fun onTitleChange(value: String) {
        updateLoaded { copy(title = value, titleError = value.isBlank()) }
    }

    fun onDescriptionChange(value: String) {
        updateLoaded { copy(description = value, descriptionError = value.isBlank()) }
    }

    fun onVisibilityChange(value: GuideVisibility) {
        updateLoaded { copy(visibility = value) }
    }

    fun onPhotoSelected(
        bytes: ByteArray,
        mimeType: String,
    ) {
        updateLoaded { copy(pendingImage = Pair(bytes, mimeType)) }
    }

    fun onDismissErrors() {
        updateLoaded { copy(validationErrors = emptyList()) }
    }

    @OptIn(ExperimentalTime::class)
    fun onSave() {
        val loaded = _state.value as? EditGuideUiState.Loaded ?: return
        if (loaded.isSaving || loaded.isUploadingPhoto) return

        val errors =
            buildList {
                if (loaded.title.isBlank()) add(ErrorEntity.Validation.EmptyTitle)
                if (loaded.description.isBlank()) add(ErrorEntity.Validation.EmptyDescription)
                if (loaded.visibility == GuideVisibility.PUBLIC) {
                    if (loaded.restaurants.size < 3) add(ErrorEntity.Validation.PublicGuideNeedsMoreRestaurants)
                    if (loaded.imageUrl == null && loaded.pendingImage == null) {
                        add(ErrorEntity.Validation.PublicGuideNeedsImage)
                    }
                }
            }
        if (errors.isNotEmpty()) {
            updateLoaded {
                copy(
                    titleError = ErrorEntity.Validation.EmptyTitle in errors,
                    descriptionError = ErrorEntity.Validation.EmptyDescription in errors,
                    validationErrors = errors,
                )
            }
            return
        }

        viewModelScope.launch {
            if (loaded.pendingImage != null) {
                updateLoaded { copy(isUploadingPhoto = true) }

                val (bytes, mimeType) = loaded.pendingImage
                val ext =
                    when (mimeType) {
                        "image/png" -> "png"
                        "image/webp" -> "webp"
                        else -> "jpg"
                    }
                val fileName = "photo_${Clock.System.now().toEpochMilliseconds()}.$ext"
                val presigned = guidesRepository.getPhotoPresignedUrl(guideId, fileName, mimeType)
                if (presigned is Result.Success) {
                    if (uploadPhoto(presigned.data, bytes, mimeType) is Result.Success) {
                        val addResult = guidesRepository.addPhoto(guideId, presigned.data.publicUrl)
                        if (addResult is Result.Success) {
                            updateLoaded { copy(imageUrl = presigned.data.publicUrl) }
                        }
                    }
                }

                updateLoaded { copy(isUploadingPhoto = false, pendingImage = null) }
            }

            val current = _state.value as? EditGuideUiState.Loaded ?: return@launch
            updateLoaded { copy(isSaving = true) }

            when (
                updateGuide(
                    id = guideId,
                    title = current.title,
                    description = current.description,
                    restaurantIds = current.restaurants.map { it.id },
                    visibility = current.visibility,
                )
            ) {
                is Result.Error -> updateLoaded { copy(isSaving = false) }
                is Result.Success -> _events.emit(UiEvent.NavigateBack)
            }
        }
    }

    fun onDelete() {
        val loaded = _state.value as? EditGuideUiState.Loaded ?: return
        if (loaded.isDeleting || loaded.isSaving || loaded.isUploadingPhoto) return
        viewModelScope.launch {
            updateLoaded { copy(isDeleting = true) }
            when (deleteGuide(guideId)) {
                is Result.Error -> updateLoaded { copy(isDeleting = false) }
                is Result.Success -> _events.emit(UiEvent.GuideDeleted)
            }
        }
    }

    fun onRetry() {
        loadGuide(guideId)
    }

    sealed class UiEvent {
        data object NavigateBack : UiEvent()

        data object GuideDeleted : UiEvent()
    }
}
