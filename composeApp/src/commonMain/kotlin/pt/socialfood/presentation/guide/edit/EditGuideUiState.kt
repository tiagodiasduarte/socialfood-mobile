package pt.socialfood.presentation.guide.edit

import pt.socialfood.domain.error.ErrorEntity
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.domain.model.Restaurant

sealed interface EditGuideUiState {
    data object Loading : EditGuideUiState
    data class Loaded(
        val guide: Guide,
        val title: String,
        val description: String,
        val visibility: GuideVisibility,
        val titleError: Boolean = false,
        val descriptionError: Boolean = false,
        val restaurants: List<Restaurant>,
        val imageUrl: String? = null,
        val pendingImage: Pair<ByteArray, String>? = null,
        val isUploadingPhoto: Boolean = false,
        val isSaving: Boolean = false,
        val isDeleting: Boolean = false,
        val validationErrors: List<ErrorEntity.Validation> = emptyList(),
    ) : EditGuideUiState
    data class Error(val message: String) : EditGuideUiState
}
