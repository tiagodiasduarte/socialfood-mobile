package pt.socialfood.feature.guide.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pt.socialfood.core.common.Result
import pt.socialfood.core.domain.repository.GuidesRepository
import pt.socialfood.core.domain.repository.PhotosRepository
import pt.socialfood.core.domain.usecase.guide.UpdateGuideUseCase
import pt.socialfood.core.domain.usecase.user.ObserveUserUseCase
import pt.socialfood.core.model.GuideVisibility
import pt.socialfood.core.ui.error.toErrorCode
import pt.socialfood.feature.guide.impl.generated.resources.Res
import pt.socialfood.feature.guide.impl.generated.resources.edit_guide_details_description_error
import pt.socialfood.feature.guide.impl.generated.resources.edit_guide_details_title_error
import pt.socialfood.feature.restaurant.navigation.RestaurantPickerResults

class EditGuideViewModel(
    private val updateGuide: UpdateGuideUseCase,
    private val photosRepository: PhotosRepository,
    private val guidesRepository: GuidesRepository,
    private val observeUser: ObserveUserUseCase,
    private val restaurantPickerResults: RestaurantPickerResults,
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

    /** Request key for the restaurant picker opened from this guide. */
    val restaurantPickerKey = "edit-guide:$guideId"

    fun onRestaurantRemoved(placeId: String) {
        updateLoaded { copy(restaurants = restaurants.filter { it.id != placeId }) }
    }

    init {
        load()
        viewModelScope.launch {
            // Picked restaurants are only added to a loaded guide, so wait for it before collecting.
            state.first { it is EditGuideUiState.Loaded }
            restaurantPickerResults.results(restaurantPickerKey).collect { restaurant ->
                updateLoaded {
                    if (restaurants.any { it.id == restaurant.id }) return@updateLoaded this
                    copy(restaurants = restaurants + restaurant)
                }
            }
        }
    }

    fun load() {
        viewModelScope.launch {
            _state.value = EditGuideUiState.Loading

            val guideDeferred = async { guidesRepository.findById(guideId) }
            val userDeferred = async { observeUser().first() }
            val result = guideDeferred.await()
            val currentUser = userDeferred.await()

            when (result) {
                is Result.Success ->
                    _state.value =
                        EditGuideUiState.Loaded(
                            guide = result.data,
                            title = result.data.name,
                            description = result.data.description,
                            visibility = result.data.visibility,
                            restaurants = result.data.restaurants,
                            imageUrl = result.data.imageUrl,
                            isAuthor = currentUser?.isAuthor ?: false,
                        )
                is Result.Failure -> _state.value = EditGuideUiState.Error(result.error.toErrorCode())
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

    fun onPhotoSelected(bytes: ByteArray, mimeType: String) {
        updateLoaded { copy(pendingImage = Pair(bytes, mimeType)) }
    }

    fun onDismissErrors() {
        updateLoaded { copy(validationErrors = emptyList()) }
    }

    fun onSave() {
        val loaded = _state.value as? EditGuideUiState.Loaded
        if (loaded == null || loaded.isSaving || loaded.isUploadingPhoto) return

        val errors = loaded.validationErrors()
        if (errors.isNotEmpty()) {
            updateLoaded {
                copy(
                    titleError = Res.string.edit_guide_details_title_error in errors,
                    descriptionError = Res.string.edit_guide_details_description_error in errors,
                    validationErrors = errors,
                )
            }
            return
        }

        viewModelScope.launch {
            loaded.pendingImage?.let { (bytes, mimeType) ->
                updateLoaded { copy(isUploadingPhoto = true) }
                val imageUrl = uploadGuidePhoto(guidesRepository, photosRepository, guideId, bytes, mimeType)
                if (imageUrl != null) updateLoaded { copy(imageUrl = imageUrl) }
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
                is Result.Failure -> updateLoaded { copy(isSaving = false) }
                is Result.Success -> _events.emit(UiEvent.NavigateBack)
            }
        }
    }

    fun onDelete() {
        val loaded = _state.value as? EditGuideUiState.Loaded ?: return
        if (loaded.isDeleting || loaded.isSaving || loaded.isUploadingPhoto) return
        viewModelScope.launch {
            updateLoaded { copy(isDeleting = true) }
            when (guidesRepository.delete(guideId)) {
                is Result.Failure -> updateLoaded { copy(isDeleting = false) }
                is Result.Success -> _events.emit(UiEvent.GuideDeleted)
            }
        }
    }

    sealed class UiEvent {
        data object NavigateBack : UiEvent()

        data object GuideDeleted : UiEvent()
    }
}
